// Actualizar: src/main/java/com/fantasyfightleague/dto/CreatePublicLeagueDTO.java
package com.fantasyfightleague.dto;

public class CreatePublicLeagueDTO {
    private String name;
    private String description;
    private Long eventId;
    
    // Parámetros opcionales con valores por defecto
    private Integer initialBudget = 100000; // 100,000 por defecto
    private Integer maxFighters = 10; // 10 por defecto
    private Integer maxFightersEvent = 3; // 3 por defecto
    private Integer minFightersEvent = 1; // 1 por defecto
    
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
    
    public Integer getInitialBudget() {
        return initialBudget;
    }
    
    public void setInitialBudget(Integer initialBudget) {
        this.initialBudget = initialBudget;
    }
    
    public Integer getMaxFighters() {
        return maxFighters;
    }
    
    public void setMaxFighters(Integer maxFighters) {
        this.maxFighters = maxFighters;
    }
    
    public Integer getMaxFightersEvent() {
        return maxFightersEvent;
    }
    
    public void setMaxFightersEvent(Integer maxFightersEvent) {
        this.maxFightersEvent = maxFightersEvent;
    }
    
    public Integer getMinFightersEvent() {
        return minFightersEvent;
    }
    
    public void setMinFightersEvent(Integer minFightersEvent) {
        this.minFightersEvent = minFightersEvent;
    }
}