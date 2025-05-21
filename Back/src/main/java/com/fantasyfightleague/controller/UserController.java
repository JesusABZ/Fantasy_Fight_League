// Crear este archivo en: src/main/java/com/fantasyfightleague/controller/UserController.java
package com.fantasyfightleague.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.dto.PasswordChangeDTO;
import com.fantasyfightleague.dto.ProfileUpdateDTO;
import com.fantasyfightleague.model.User;
import com.fantasyfightleague.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserProfile() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        
        Optional<User> user = userService.findByUsername(username);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // No enviamos la contraseña
        User userProfile = user.get();
        userProfile.setPassword(null);
        
        return ResponseEntity.ok(userProfile);
    }
    
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileUpdateDTO profileDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        
        if (profileDTO.getFirstName() != null) {
            user.setFirstName(profileDTO.getFirstName());
        }
        
        if (profileDTO.getLastName() != null) {
            user.setLastName(profileDTO.getLastName());
        }
        
        if (profileDTO.getProfileImageUrl() != null) {
            user.setProfileImageUrl(profileDTO.getProfileImageUrl());
        }
        
        userService.saveUser(user);
        
        // No enviamos la contraseña
        user.setPassword(null);
        
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDTO passwordDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        
        // Aquí podríamos validar la contraseña actual antes de cambiarla
        // Por simplicidad, simplemente cambiamos la contraseña
        
        user.setPassword(passwordDTO.getNewPassword());
        userService.saveUser(user); // El servicio se encarga de encriptar la contraseña
        
        return ResponseEntity.ok(new MessageResponseDTO("Contraseña actualizada con éxito"));
    }
}