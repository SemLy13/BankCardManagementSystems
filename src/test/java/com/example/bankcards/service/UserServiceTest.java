package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Тесты для UserService
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
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
    }

    @Test
    void whenCreateUserWithValidData_thenUserIsCreated() {
        // given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User userToCreate = new User();
        userToCreate.setUsername("newuser");
        userToCreate.setEmail("new@example.com");
        userToCreate.setPassword("password");

        // when
        User createdUser = userService.createUser(userToCreate);

        // then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("newuser");
        verify(userRepository).save(userToCreate);
    }

    @Test
    void whenCreateUserWithExistingUsername_thenThrowsBusinessException() {
        // given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        User userToCreate = new User();
        userToCreate.setUsername("testuser");
        userToCreate.setEmail("test@example.com");
        userToCreate.setPassword("password");

        // when & then
        assertThatThrownBy(() -> userService.createUser(userToCreate))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", "USER_ALREADY_EXISTS")
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.CONFLICT);
    }

    @Test
    void whenCreateUserWithExistingEmail_thenThrowsBusinessException() {
        // given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        User userToCreate = new User();
        userToCreate.setUsername("newuser");
        userToCreate.setEmail("test@example.com");
        userToCreate.setPassword("password");

        // when & then
        assertThatThrownBy(() -> userService.createUser(userToCreate))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", "USER_EMAIL_ALREADY_EXISTS")
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.CONFLICT);
    }

    @Test
    void whenFindById_thenReturnUser() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        Optional<User> found = userService.findById(1L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenFindByIdNotFound_thenReturnEmpty() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<User> found = userService.findById(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void whenUpdateUserWithValidData_thenUserIsUpdated() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setUsername("updateduser");
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setPassword("newpassword");

        // when
        User updatedUser = userService.updateUser(userToUpdate);

        // then
        assertThat(updatedUser).isNotNull();
        verify(userRepository).save(userToUpdate);
    }

    @Test
    void whenUpdateNonExistentUser_thenThrowsResourceNotFoundException() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User userToUpdate = new User();
        userToUpdate.setId(999L);
        userToUpdate.setUsername("testuser");
        userToUpdate.setEmail("test@example.com");

        // when & then
        assertThatThrownBy(() -> userService.updateUser(userToUpdate))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void whenDeactivateUser_thenUserIsDisabled() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        userService.deactivateUser(1L);

        // then
        assertThat(testUser.getEnabled()).isFalse();
        verify(userRepository).save(testUser);
    }

    @Test
    void whenDeactivateNonExistentUser_thenThrowsResourceNotFoundException() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deactivateUser(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void whenActivateUser_thenUserIsEnabled() {
        // given
        testUser.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        userService.activateUser(1L);

        // then
        assertThat(testUser.getEnabled()).isTrue();
        verify(userRepository).save(testUser);
    }

    @Test
    void whenDeleteUser_thenUserIsDeleted() {
        // given
        when(userRepository.existsById(1L)).thenReturn(true);

        // when
        userService.deleteUser(1L);

        // then
        verify(userRepository).deleteById(1L);
    }

    @Test
    void whenDeleteNonExistentUser_thenThrowsResourceNotFoundException() {
        // given
        when(userRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void whenFindByUsername_thenReturnUser() {
        // given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // when
        Optional<User> found = userService.findByUsername("testuser");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // when
        Optional<User> found = userService.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void whenFindAll_thenReturnAllUsers() {
        // given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // when
        List<User> found = userService.findAll();

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenFindActiveUsers_thenReturnOnlyActiveUsers() {
        // given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userRepository.findByEnabledTrue()).thenReturn(activeUsers);

        // when
        List<User> found = userService.findActiveUsers();

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getEnabled()).isTrue();
    }

    @Test
    void whenFindByName_thenReturnMatchingUsers() {
        // given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByNameContaining("Test")).thenReturn(users);

        // when
        List<User> found = userService.findByName("Test");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFirstName()).isEqualTo("Test");
    }

    @Test
    void whenFindByRole_thenReturnUsersWithRole() {
        // given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByRoleName("USER")).thenReturn(users);

        // when
        List<User> found = userService.findByRole("USER");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenExistsByUsername_thenReturnCorrectResult() {
        // given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // when
        boolean exists = userService.existsByUsername("testuser");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByEmail_thenReturnCorrectResult() {
        // given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // when
        boolean exists = userService.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void whenGetUserCardsCount_thenReturnCorrectCount() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        long count = userService.getUserCardsCount(1L);

        // then
        assertThat(count).isEqualTo(0); // Тестовый пользователь не имеет карт
    }
}
