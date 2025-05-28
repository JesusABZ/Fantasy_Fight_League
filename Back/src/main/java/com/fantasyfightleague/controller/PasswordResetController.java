// Back/src/main/java/com/fantasyfightleague/controller/PasswordResetController.java
package com.fantasyfightleague.controller;

import com.fantasyfightleague.dto.ForgotPasswordDTO;
import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.dto.ResetPasswordDTO;
import com.fantasyfightleague.dto.ValidateResetTokenDTO;
import com.fantasyfightleague.model.PasswordResetToken;
import com.fantasyfightleague.service.PasswordResetService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    /**
     * Solicitar recuperación de contraseña
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        try {
            logger.info("Solicitud de recuperación de contraseña para: {}", forgotPasswordDTO.getEmail());
            
            passwordResetService.sendPasswordResetEmail(forgotPasswordDTO.getEmail());
            
            // Siempre devolver éxito por seguridad (no revelar si el email existe)
            return ResponseEntity.ok(new MessageResponseDTO(
                "Si el email está registrado en nuestro sistema, recibirás un enlace de recuperación en unos minutos. " +
                "Revisa tu bandeja de entrada y carpeta de spam."
            ));
            
        } catch (Exception e) {
            logger.error("Error en solicitud de recuperación: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new MessageResponseDTO("Error interno del servidor. Inténtalo más tarde."));
        }
    }
    
    /**
     * Validar token de reset (para verificar si el enlace es válido)
     */
    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam("token") String token) {
        try {
            logger.info("Validando token de reset: {}", token);
            
            PasswordResetToken resetToken = passwordResetService.validateResetToken(token);
            
            return ResponseEntity.ok(new ValidateResetTokenDTO(
                true, 
                "Token válido",
                resetToken.getUser().getEmail() // Para mostrar el email en el frontend
            ));
            
        } catch (Exception e) {
            logger.warn("Token de reset inválido: {} - Error: {}", token, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ValidateResetTokenDTO(false, e.getMessage()));
        }
    }
    
    /**
     * Cambiar contraseña usando token de reset
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        try {
            logger.info("Cambiando contraseña con token: {}", resetPasswordDTO.getToken());
            
            // Validar que las contraseñas coincidan
            if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponseDTO("Las contraseñas no coinciden"));
            }
            
            // Validar longitud mínima
            if (resetPasswordDTO.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponseDTO("La contraseña debe tener al menos 6 caracteres"));
            }
            
            // Cambiar la contraseña
            passwordResetService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
            
            return ResponseEntity.ok(new MessageResponseDTO(
                "¡Contraseña cambiada exitosamente! Ya puedes iniciar sesión con tu nueva contraseña."
            ));
            
        } catch (Exception e) {
            logger.error("Error al cambiar contraseña: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new MessageResponseDTO(e.getMessage()));
        }
    }
}