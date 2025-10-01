package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Найти пользователя по username
     */
    Optional<User> findByUsername(String username);

    /**
     * Найти пользователя по email
     */
    Optional<User> findByEmail(String email);

    /**
     * Найти пользователя по username или email
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Проверить, существует ли пользователь с данным username
     */
    boolean existsByUsername(String username);

    /**
     * Проверить, существует ли пользователь с данным email
     */
    boolean existsByEmail(String email);

    /**
     * Найти всех активных пользователей
     */
    List<User> findByEnabledTrue();

    /**
     * Найти пользователей по имени (поиск по firstName или lastName)
     */
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);

    /**
     * Найти пользователей по роли
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
}
