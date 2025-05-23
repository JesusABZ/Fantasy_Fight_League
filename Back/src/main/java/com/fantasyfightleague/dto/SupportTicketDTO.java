// Crear: src/main/java/com/fantasyfightleague/dto/SupportTicketDTO.java
package com.fantasyfightleague.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para tickets de soporte
 */
public class SupportTicketDTO {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    private String username; // Opcional, puede ser null si no está registrado
    
    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede tener más de 200 caracteres")
    private String subject;
    
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 2000, message = "El mensaje no puede tener más de 2000 caracteres")
    private String message;
    
    private String category; // "BUG", "FEATURE_REQUEST", "ACCOUNT_ISSUE", "GENERAL", etc.
    
    private String userAgent; // Información del navegador (opcional)
    
    // Constructores
    public SupportTicketDTO() {
    }
    
    // Getters y setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}