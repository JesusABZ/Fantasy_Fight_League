// Crear: src/main/java/com/fantasyfightleague/controller/RoleController.java
package com.fantasyfightleague.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.model.ERole;
import com.fantasyfightleague.model.Role;
import com.fantasyfightleague.model.User;
import com.fantasyfightleague.repository.RoleRepository;
import com.fantasyfightleague.service.UserService;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @PostMapping("/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
        
        user.addRole(adminRole);
        userService.saveUser(user);
        
        return ResponseEntity.ok(new MessageResponseDTO("Usuario " + user.getUsername() + " promovido a administrador"));
    }
    
    @PostMapping("/demote/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> demoteUserFromAdmin(@PathVariable Long userId) {
        Optional<User> userOpt = userService.findById(userId);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
        
        user.removeRole(adminRole);
        userService.saveUser(user);
        
        return ResponseEntity.ok(new MessageResponseDTO("Rol de administrador removido del usuario " + user.getUsername()));
    }
}