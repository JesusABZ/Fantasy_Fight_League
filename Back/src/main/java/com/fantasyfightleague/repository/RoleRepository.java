// Crear: src/main/java/com/fantasyfightleague/repository/RoleRepository.java
package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.ERole;
import com.fantasyfightleague.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    boolean existsByName(ERole name);
}