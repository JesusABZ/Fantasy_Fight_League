package com.fantasyfightleague.service;

import com.fantasyfightleague.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    // Guardar un nuevo usuario o actualizar uno existente
    User saveUser(User user);
    
    // Buscar un usuario por su ID
    Optional<User> findById(Long id);
    
    // Buscar un usuario por su nombre de usuario
    Optional<User> findByUsername(String username);
    
    // Buscar un usuario por su email
    Optional<User> findByEmail(String email);
    
    // Obtener todos los usuarios
    List<User> findAllUsers();
    
    // Verificar si existe un usuario con un nombre de usuario específico
    boolean existsByUsername(String username);
    
    // Verificar si existe un usuario con un email específico
    boolean existsByEmail(String email);
    
    // Eliminar un usuario por su ID
    void deleteUser(Long id);
}