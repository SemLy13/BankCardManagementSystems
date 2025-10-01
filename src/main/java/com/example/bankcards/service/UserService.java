package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями
 * Содержит бизнес-логику и транзакционные операции
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Создать нового пользователя
     */
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
        return userRepository.save(user);
    }

    /**
     * Найти пользователя по ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Найти пользователя по username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Найти пользователя по email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Найти пользователя по username или email
     */
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    /**
     * Получить всех пользователей
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Найти активных пользователей
     */
    public List<User> findActiveUsers() {
        return userRepository.findByEnabledTrue();
    }

    /**
     * Найти пользователей по имени
     */
    public List<User> findByName(String name) {
        return userRepository.findByNameContaining(name);
    }

    /**
     * Найти пользователей по роли
     */
    public List<User> findByRole(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    /**
     * Обновить пользователя
     */
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + user.getId()));

        // Проверка уникальности username
        if (!existingUser.getUsername().equals(user.getUsername()) &&
            userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("USER_ALREADY_EXISTS",
                "Пользователь с таким username уже существует", HttpStatus.CONFLICT);
        }

        // Проверка уникальности email
        if (!existingUser.getEmail().equals(user.getEmail()) &&
            userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("USER_EMAIL_ALREADY_EXISTS",
                "Пользователь с таким email уже существует", HttpStatus.CONFLICT);
        }

        return userRepository.save(user);
    }

    /**
     * Деактивировать пользователя
     */
    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * Активировать пользователя
     */
    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с ID: " + id));
        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Удалить пользователя
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользователь не найден с ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Проверить существование пользователя по username
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Проверить существование пользователя по email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Получить количество карт пользователя
     */
    public long getUserCardsCount(Long userId) {
        return userRepository.findById(userId)
                .map(user -> (long) user.getCards().size())
                .orElse(0L);
    }
}
