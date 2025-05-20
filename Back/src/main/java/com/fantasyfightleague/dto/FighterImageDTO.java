package com.fantasyfightleague.dto;

/**
 * DTO para actualizar la imagen de un luchador
 */
public class FighterImageDTO {
    
    private Long fighterId;
    private String imageUrl;
    
    // Constructores
    public FighterImageDTO() {
    }
    
    public FighterImageDTO(Long fighterId, String imageUrl) {
        this.fighterId = fighterId;
        this.imageUrl = imageUrl;
    }
    
    // Getters y setters
    public Long getFighterId() {
        return fighterId;
    }
    
    public void setFighterId(Long fighterId) {
        this.fighterId = fighterId;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}