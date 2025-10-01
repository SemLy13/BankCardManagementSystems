package com.example.bankcards.repository;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для UserRepository
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never"
})
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Создаем тестовую роль
        testRole = new Role();
        testRole.setName("USER");
        testRole.setDescription("Regular user role");
        entityManager.persist(testRole);

        // Создаем тестового пользователя
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
        testUser.getRoles().add(testRole);
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    void whenFindByUsername_thenReturnUser() {
        // given
        String username = "testuser";

        // when
        Optional<User> found = userRepository.findByUsername(username);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(username);
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        // given
        String email = "test@example.com";

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenFindByUsernameOrEmail_thenReturnUser() {
        // when
        Optional<User> foundByUsername = userRepository.findByUsernameOrEmail("testuser", "wrong@example.com");
        Optional<User> foundByEmail = userRepository.findByUsernameOrEmail("wronguser", "test@example.com");

        // then
        assertThat(foundByUsername).isPresent();
        assertThat(foundByEmail).isPresent();
        assertThat(foundByUsername.get().getId()).isEqualTo(foundByEmail.get().getId());
    }

    @Test
    void whenExistsByUsername_thenReturnTrue() {
        // when
        boolean exists = userRepository.existsByUsername("testuser");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() {
        // when
        boolean exists = userRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void whenFindByEnabledTrue_thenReturnActiveUsers() {
        // given
        User disabledUser = new User();
        disabledUser.setUsername("disabled");
        disabledUser.setEmail("disabled@example.com");
        disabledUser.setPassword("password");
        disabledUser.setEnabled(false);
        entityManager.persist(disabledUser);

        // when
        List<User> activeUsers = userRepository.findByEnabledTrue();

        // then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getUsername()).isEqualTo("testuser");
        assertThat(activeUsers.get(0).getEnabled()).isTrue();
    }

    @Test
    void whenFindByNameContaining_thenReturnMatchingUsers() {
        // given
        User anotherUser = new User();
        anotherUser.setUsername("another");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setFirstName("John");
        anotherUser.setLastName("Doe");
        entityManager.persist(anotherUser);

        // when
        List<User> foundByFirstName = userRepository.findByNameContaining("Test");
        List<User> foundByLastName = userRepository.findByNameContaining("Doe");

        // then
        assertThat(foundByFirstName).hasSize(1);
        assertThat(foundByFirstName.get(0).getFirstName()).isEqualTo("Test");

        assertThat(foundByLastName).hasSize(1);
        assertThat(foundByLastName.get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    void whenFindByRoleName_thenReturnUsersWithRole() {
        // when
        List<User> usersWithRole = userRepository.findByRoleName("USER");

        // then
        assertThat(usersWithRole).hasSize(1);
        assertThat(usersWithRole.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenSaveUser_thenUserIsPersisted() {
        // given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");
        newUser.setFirstName("New");
        newUser.setLastName("User");

        // when
        User saved = userRepository.save(newUser);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void whenDeleteUser_thenUserIsRemoved() {
        // given
        Long userId = testUser.getId();

        // when
        userRepository.deleteById(userId);

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
    }
}
