// UpdatePickDTO.java
package com.fantasyfightleague.dto;

import java.util.List;

public class UpdatePickDTO {
    private List<Long> fighterIds; // IDs de los luchadores elegidos (1-3)
    
    // Constructores
    public UpdatePickDTO() {
    }
    
    // Getters y setters
    public List<Long> getFighterIds() {
        return fighterIds;
    }
    
    public void setFighterIds(List<Long> fighterIds) {
        this.fighterIds = fighterIds;
    }
}
