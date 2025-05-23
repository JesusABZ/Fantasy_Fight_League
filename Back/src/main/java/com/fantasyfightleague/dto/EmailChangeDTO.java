// Crear este archivo en: src/main/java/com/fantasyfightleague/dto/EmailChangeDTO.java
package com.fantasyfightleague.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailChangeDTO {
    
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String currentPassword;
    
    @NotBlank(message = "El nuevo email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String newEmail;
    
    // Constructores
    public EmailChangeDTO() {
    }
    
    public EmailChangeDTO(String currentPassword, String newEmail) {
        this.currentPassword = currentPassword;
        this.newEmail = newEmail;
    }
    
    // Getters y setters
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewEmail() {
        return newEmail;
    }
    
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}