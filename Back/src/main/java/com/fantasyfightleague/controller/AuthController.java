// Crear este archivo en: src/main/java/com/fantasyfightleague/controller/AuthController.java
package com.fantasyfightleague.controller;

import java.util.Date;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.fantasyfightleague.dto.JwtResponseDTO;
import com.fantasyfightleague.dto.LoginRequestDTO;
import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.dto.SignupRequestDTO;
import com.fantasyfightleague.model.User;
import com.fantasyfightleague.model.VerificationToken;
import com.fantasyfightleague.security.jwt.JwtUtils;
import com.fantasyfightleague.security.services.UserDetailsImpl;
import com.fantasyfightleague.service.EmailVerificationService;
import com.fantasyfightleague.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    EmailVerificationService emailVerificationService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return ResponseEntity.ok(new JwtResponseDTO(jwt,
                                                 userDetails.getId(),
                                                 userDetails.getUsername(),
                                                 userDetails.getEmail(),
                                                 userDetails.isEmailConfirmed()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDTO signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDTO("Error: El nombre de usuario ya está en uso"));
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDTO("Error: El email ya está en uso"));
        }

        // Crear nuevo usuario
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getPassword(),
                signUpRequest.getEmail());
        
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmailConfirmed(false); // Por defecto, email no confirmado

        userService.saveUser(user);

        // Enviar email de verificación
        emailVerificationService.sendVerificationEmail(user);

        return ResponseEntity.ok(new MessageResponseDTO("¡Usuario registrado con éxito! Por favor, verifica tu correo electrónico"));
    }
    
    @GetMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        VerificationToken verificationToken = emailVerificationService.getVerificationToken(token);
        
        if (verificationToken == null) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Token de verificación inválido"));
        }
        
        User user = verificationToken.getUser();
        
        if (verificationToken.getExpiryDate().before(new Date())) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("El token ha expirado"));
        }
        
        user.setEmailConfirmed(true);
        userService.saveUser(user);
        
        return ResponseEntity.ok(new MessageResponseDTO("Email verificado correctamente. Ya puedes iniciar sesión."));
    }
}