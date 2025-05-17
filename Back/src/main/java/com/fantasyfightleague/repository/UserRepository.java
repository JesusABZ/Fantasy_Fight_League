package com.fantasyfightleague.repository;

import com.fantasyfightleague.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Buscar usuario por nombre de usuario
    Optional<User> findByUsername(String username);
    
    // Buscar usuario por email
    Optional<User> findByEmail(String email);
    
    // Verificar si existe un usuario con un nombre de usuario específico
    boolean existsByUsername(String username);
    
    // Verificar si existe un usuario con un email específico
    boolean existsByEmail(String email);
}