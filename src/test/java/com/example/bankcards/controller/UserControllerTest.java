package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserUpdateRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для UserController
 */
@WebMvcTest(UserController.class)
@Import(com.example.bankcards.TestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDto testUserDto;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("USER");
        testRole.setDescription("Regular user role");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        testUser.getRoles().add(testRole);

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setFirstName("Test");
        testUserDto.setLastName("User");
        testUserDto.setEnabled(true);
        testUserDto.setRoles(java.util.Set.of("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetAllUsers_thenReturnUsersList() throws Exception {
        // given
        List<User> users = Arrays.asList(testUser);
        when(userService.findAll()).thenReturn(users);

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetUserById_thenReturnUser() throws Exception {
        // given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // when & then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetUserByIdNotFound_thenReturnNotFound() throws Exception {
        // given
        when(userService.findById(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGetUserByUsername_thenReturnUser() throws Exception {
        // given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // when & then
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenCreateUser_thenReturnCreatedUser() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setFirstName("New");
        request.setLastName("User");

        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // when & then
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenCreateUserWithInvalidData_thenReturnBadRequest() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(""); // Invalid empty username
        request.setEmail("invalid-email"); // Invalid email format
        request.setPassword("123"); // Password too short

        // when & then
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("updateduser");
        request.setEmail("updated@example.com");
        request.setFirstName("Updated");
        request.setLastName("User");

        when(userService.updateUser(any(User.class))).thenReturn(testUser);

        // when & then
        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenDeleteUser_thenReturnSuccess() throws Exception {
        // given
        // UserService.deleteUser() doesn't throw exception for successful deletion

        // when & then
        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь успешно удален"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenActivateUser_thenReturnSuccess() throws Exception {
        // given
        // UserService.activateUser() doesn't throw exception for successful activation

        // when & then
        mockMvc.perform(post("/api/users/1/activate").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь успешно активирован"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenDeactivateUser_thenReturnSuccess() throws Exception {
        // given
        // UserService.deactivateUser() doesn't throw exception for successful deactivation

        // when & then
        mockMvc.perform(post("/api/users/1/deactivate").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь успешно деактивирован"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenSearchUsersByName_thenReturnMatchingUsers() throws Exception {
        // given
        List<User> users = Arrays.asList(testUser);
        when(userService.findByName("Test")).thenReturn(users);

        // when & then
        mockMvc.perform(get("/api/users/search?name=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenSearchUsersByRole_thenReturnUsersWithRole() throws Exception {
        // given
        List<User> users = Arrays.asList(testUser);
        when(userService.findByRole("USER")).thenReturn(users);

        // when & then
        mockMvc.perform(get("/api/users/search?role=USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserAccessesOwnData_thenReturnUser() throws Exception {
        // given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // when & then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void whenUserAccessesOtherUserData_thenReturnForbidden() throws Exception {
        // given
        when(userService.findById(2L)).thenReturn(Optional.of(testUser));

        // when & then
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenUnauthenticatedUserAccessesEndpoint_thenReturnUnauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }
}
