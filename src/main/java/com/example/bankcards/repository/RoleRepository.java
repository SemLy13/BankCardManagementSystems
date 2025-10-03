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

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    List<Role> findAll();

    Set<Role> findByNameIn(Set<String> names);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.users WHERE r.id IN :roleIds")
    List<Role> findByIdInWithUsers(@Param("roleIds") List<Long> roleIds);
}
