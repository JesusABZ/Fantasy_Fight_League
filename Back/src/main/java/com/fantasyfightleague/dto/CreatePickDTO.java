// CreatePickDTO.java
package com.fantasyfightleague.dto;

import java.util.List;

public class CreatePickDTO {
    private Long leagueId;
    private Long eventId;
    private List<Long> fighterIds; // IDs de los luchadores elegidos (1-3)
    
    // Constructores
    public CreatePickDTO() {
    }
    
    // Getters y setters
    public Long getLeagueId() {
        return leagueId;
    }
    
    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public List<Long> getFighterIds() {
        return fighterIds;
    }
    
    public void setFighterIds(List<Long> fighterIds) {
        this.fighterIds = fighterIds;
    }
}
