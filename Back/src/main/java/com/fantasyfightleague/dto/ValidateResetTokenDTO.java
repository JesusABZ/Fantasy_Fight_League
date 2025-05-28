package com.fantasyfightleague.dto;

public class ValidateResetTokenDTO {
    
    private boolean valid;
    private String message;
    private String userEmail; // Para mostrar información del usuario
    
    public ValidateResetTokenDTO() {
    }
    
    public ValidateResetTokenDTO(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    
    public ValidateResetTokenDTO(boolean valid, String message, String userEmail) {
        this.valid = valid;
        this.message = message;
        this.userEmail = userEmail;
    }
    
    // Getters y setters
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}