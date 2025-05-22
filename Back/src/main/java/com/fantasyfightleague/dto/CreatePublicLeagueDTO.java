// Crear: src/main/java/com/fantasyfightleague/dto/CreatePublicLeagueDTO.java
package com.fantasyfightleague.dto;

public class CreatePublicLeagueDTO {
    private String name;
    private String description;
    private Long eventId;
    
    // Constructores
    public CreatePublicLeagueDTO() {
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
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}