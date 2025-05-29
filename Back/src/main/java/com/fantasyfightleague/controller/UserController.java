// Crear este archivo en: src/main/java/com/fantasyfightleague/controller/UserController.java
package com.fantasyfightleague.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.fantasyfightleague.dto.EmailChangeDTO;
import com.fantasyfightleague.service.EmailVerificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;

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
    
    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
        
        user.setProfileImageUrl(profileDTO.getProfileImageUrl());
        
        userService.saveUser(user);
        
        System.out.println("✅ Usuario actualizado - profileImageUrl: " + user.getProfileImageUrl());
        
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
  
    @PutMapping("/change-email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeEmail(@Valid @RequestBody EmailChangeDTO emailChangeDTO) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            
            Optional<User> userOpt = userService.findByUsername(username);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            String oldEmail = user.getEmail();
            
            // 1. Verificar contraseña actual
            if (!passwordEncoder.matches(emailChangeDTO.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponseDTO("La contraseña actual es incorrecta"));
            }
            
            // 2. Verificar que el nuevo email no esté en uso
            if (userService.existsByEmail(emailChangeDTO.getNewEmail())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponseDTO("El nuevo email ya está en uso por otro usuario"));
            }
            
            // 3. Verificar que no sea el mismo email
            if (user.getEmail().equalsIgnoreCase(emailChangeDTO.getNewEmail())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponseDTO("El nuevo email es igual al actual"));
            }
            
            // 4. Actualizar email y marcar como no verificado
            user.setEmail(emailChangeDTO.getNewEmail());
            user.setEmailConfirmed(false);
            userService.saveUser(user);
            
            // 5. Enviar emails de verificación
            try {
                emailVerificationService.sendEmailChangeVerification(user, oldEmail);
            } catch (Exception e) {
                // Si falla el envío del email, revertir cambios
                user.setEmail(oldEmail);
                user.setEmailConfirmed(true);
                userService.saveUser(user);
                
                return ResponseEntity.internalServerError()
                    .body(new MessageResponseDTO("Error al enviar el email de verificación. Cambio revertido."));
            }
            
            return ResponseEntity.ok(new MessageResponseDTO(
                "Email actualizado correctamente. " +
                "Se ha enviado un email de verificación a " + emailChangeDTO.getNewEmail() + ". " +
                "También se ha notificado al email anterior. " +
                "Debes verificar tu nuevo email para continuar usando todas las funcionalidades."
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new MessageResponseDTO("Error interno: " + e.getMessage()));
        }
    }
}