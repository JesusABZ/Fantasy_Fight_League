// Crear este archivo en: src/main/java/com/fantasyfightleague/controller/FileUploadController.java
package com.fantasyfightleague.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fantasyfightleague.dto.MessageResponseDTO;
import com.fantasyfightleague.model.User;
import com.fantasyfightleague.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    
    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;
    
    @Value("${app.baseUrl}")
    private String baseUrl;
    
    private final UserService userService;
    
    public FileUploadController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/profile-image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            // Obtener el usuario actual
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Generar un nombre único para el archivo
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Guardar el archivo
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Actualizar la URL de la imagen de perfil del usuario
            String imageUrl = baseUrl + "/uploads/" + fileName;
            user.setProfileImageUrl(imageUrl);
            userService.saveUser(user);
            
            return ResponseEntity.ok(new MessageResponseDTO("Imagen de perfil actualizada correctamente"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Error al subir la imagen: " + e.getMessage()));
        }
    }
}