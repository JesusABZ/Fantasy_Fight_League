// Crear: src/main/java/com/fantasyfightleague/dto/CreatePrivateLeagueDTO.java
package com.fantasyfightleague.dto;

public class CreatePrivateLeagueDTO {
    private String name;
    private String description;
    
    // Constructores
    public CreatePrivateLeagueDTO() {
    }
    
    // Getters y setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}