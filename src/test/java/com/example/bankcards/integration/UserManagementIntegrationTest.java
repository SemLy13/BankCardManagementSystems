package com.example.bankcards.integration;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для управления пользователями
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never"
})
@Transactional
public class UserManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Role testRole;

    @BeforeEach
    void setUp() {
        // Создаем тестовую роль
        testRole = new Role();
        testRole.setName("USER");
        testRole.setDescription("Regular user role");
        roleRepository.save(testRole);

        // Создаем тестового пользователя
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        testUser.getRoles().add(testRole);
        userRepository.save(testUser);
    }

    @Test
    void whenAdminCreatesUser_thenUserIsCreated() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("User");

        // when & then
        mockMvc.perform(post("/api/users")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void whenUserTriesToCreateUser_thenForbidden() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");

        // when & then
        mockMvc.perform(post("/api/users")
                        .with(user("testuser").password("password").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAdminGetsAllUsers_thenReturnsUsersList() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users")
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void whenUserGetsOwnData_thenReturnsUser() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/1")
                        .with(user("testuser").password("password").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void whenUserTriesToGetOtherUserData_thenForbidden() throws Exception {
        // given - создаем второго пользователя
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        otherUser.setPassword(passwordEncoder.encode("password"));
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");
        otherUser.setEnabled(true);
        otherUser.getRoles().add(testRole);
        User savedOtherUser = userRepository.save(otherUser);

        // when & then
        mockMvc.perform(get("/api/users/" + savedOtherUser.getId())
                        .with(user("testuser").password("password").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAdminUpdatesUser_thenUserIsUpdated() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("updateduser");
        request.setEmail("updated@example.com");
        request.setPassword("password123");
        request.setFirstName("Updated");
        request.setLastName("User");

        // when & then
        mockMvc.perform(put("/api/users/1")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    void whenAdminDeletesUser_thenUserIsDeleted() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users/1")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь успешно удален"));
    }

    @Test
    void whenUnauthenticatedUserAccessesEndpoint_thenUnauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidDataProvided_thenReturnsBadRequest() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("ab"); // Too short
        request.setEmail("invalid-email");
        request.setPassword("12345"); // Too short

        // when & then
        mockMvc.perform(post("/api/users")
                        .with(user("admin").password("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
