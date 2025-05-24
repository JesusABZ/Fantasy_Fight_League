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
        // ✅ LÓGICA CORREGIDA: Solo encriptar contraseñas no encriptadas
        if (user.getPassword() != null && !isPasswordEncrypted(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    /**
     * Verifica si una contraseña ya está encriptada con BCrypt
     * @param password la contraseña a verificar
     * @return true si está encriptada, false en caso contrario
     */
    private boolean isPasswordEncrypted(String password) {
        // Las contraseñas de BCrypt siempre empiezan con $2a$, $2b$, o $2y$
        // y tienen una longitud de 60 caracteres
        return password != null && 
               password.length() == 60 && 
               (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
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