// CORREGIR: src/main/java/com/fantasyfightleague/service/impl/UserServiceImpl.java
package com.fantasyfightleague.service.impl;

import com.fantasyfightleague.model.User;
import com.fantasyfightleague.repository.UserRepository;
import com.fantasyfightleague.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User saveUser(User user) {
        // ✅ SOLO encriptar si es un usuario NUEVO o si se está cambiando la contraseña
        if (user.getId() == null) {
            // Usuario nuevo - encriptar contraseña
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Usuario existente - verificar si se está actualizando la contraseña
            Optional<User> existingUser = userRepository.findById(user.getId());
            if (existingUser.isPresent() && !existingUser.get().getPassword().equals(user.getPassword())) {
                // Solo encriptar si la contraseña ha cambiado y no está ya encriptada
                if (!user.getPassword().startsWith("$2a$")) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
            }
        }
        return userRepository.save(user);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}