package com.fantasyfightleague.dto;

public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private boolean emailConfirmed;
    
    public JwtResponseDTO(String token, Long id, String username, String email, boolean emailConfirmed) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.emailConfirmed = emailConfirmed;
    }
    
    // Getters y setters...
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }
    
    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }
}