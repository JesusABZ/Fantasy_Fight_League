// Back/src/main/java/com/fantasyfightleague/dto/ForgotPasswordDTO.java
package com.fantasyfightleague.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordDTO {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
    
    public ForgotPasswordDTO() {
    }
    
    public ForgotPasswordDTO(String email) {
        this.email = email;
    }
    
    // Getters y setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}