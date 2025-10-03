package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("USER_ALREADY_EXISTS",
                "Пользователь с таким username уже существует", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("USER_EMAIL_ALREADY_EXISTS",
                "Пользователь с таким email уже существует", HttpStatus.CONFLICT);
        }
        
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", 
                "Роль USER не найдена в системе", HttpStatus.INTERNAL_SERVER_ERROR));
        user.getRoles().add(userRole);
        
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findActiveUsers() {
        return userRepository.findByEnabledTrue();
    }

    public List<User> findByName(String name) {
        return userRepository.findByNameContaining(name);
    }

    public List<User> findByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + user.getId()));

        if (!existingUser.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("USER_ALREADY_EXISTS",
                "Пользователь с таким username уже существует", HttpStatus.CONFLICT);
        }

        if (!existingUser.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("USER_EMAIL_ALREADY_EXISTS",
                "Пользователь с таким email уже существует", HttpStatus.CONFLICT);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользователь не найден с ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public long getUserCardsCount(Long userId) {
        return userRepository.findById(userId)
                .map(user -> (long) user.getCards().size())
                .orElse(0L);
    }
}
