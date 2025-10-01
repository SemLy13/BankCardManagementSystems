package com.example.bankcards.repository;

import com.example.bankcards.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Найти роль по имени
     */
    Optional<Role> findByName(String name);

    /**
     * Проверить, существует ли роль с данным именем
     */
    boolean existsByName(String name);

    /**
     * Найти все роли
     */
    List<Role> findAll();

    /**
     * Найти роли по списку имен
     */
    Set<Role> findByNameIn(Set<String> names);

    /**
     * Найти роли пользователей (для оптимизации загрузки)
     */
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.users WHERE r.id IN :roleIds")
    List<Role> findByIdInWithUsers(@Param("roleIds") List<Long> roleIds);
}
