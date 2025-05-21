// Crear este archivo en: src/main/java/com/fantasyfightleague/dto/PasswordChangeDTO.java
package com.fantasyfightleague.dto;

public class PasswordChangeDTO {
    private String currentPassword;
    private String newPassword;
    
    // Getters y setters
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}