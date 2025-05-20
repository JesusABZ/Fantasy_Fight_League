package com.fantasyfightleague.dto;

/**
 * DTO para actualizar el precio de un luchador
 */
public class FighterPriceDTO {
    
    private Long fighterId;
    private Integer price;
    
    // Para el cálculo automático
    private String position; // "MAIN_EVENT", "CO_MAIN", "MAIN_CARD", "PRELIMS", "EARLY_PRELIMS"
    private Boolean isFavorite;
    private String ranking; // "C" para campeón, números del "1" al "15" para rankings, o null/vacío si no está rankeado
    
    // Constructores
    public FighterPriceDTO() {
    }
    
    public FighterPriceDTO(Long fighterId, Integer price) {
        this.fighterId = fighterId;
        this.price = price;
    }
    
    public FighterPriceDTO(Long fighterId, String position, Boolean isFavorite, String ranking) {
        this.fighterId = fighterId;
        this.position = position;
        this.isFavorite = isFavorite;
        this.ranking = ranking;
    }
    
    // Getters y setters
    public Long getFighterId() {
        return fighterId;
    }
    
    public void setFighterId(Long fighterId) {
        this.fighterId = fighterId;
    }
    
    public Integer getPrice() {
        return price;
    }
    
    public void setPrice(Integer price) {
        this.price = price;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public Boolean getIsFavorite() {
        return isFavorite;
    }
    
    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
    
    public String getRanking() {
        return ranking;
    }
    
    public void setRanking(String ranking) {
        this.ranking = ranking;
    }
    
    /**
     * Determina si el luchador está rankeado
     * @return true si tiene un ranking (C o número), false en caso contrario
     */
    public boolean isRanked() {
        return ranking != null && !ranking.trim().isEmpty();
    }
    
    /**
     * Determina si el luchador es campeón
     * @return true si el ranking es "C", false en caso contrario
     */
    public boolean isChampion() {
        return "C".equalsIgnoreCase(ranking);
    }
    
    /**
     * Obtiene el ranking numérico del luchador
     * @return El número de ranking (1-15) o 0 si no es un número válido
     */
    public int getRankingNumber() {
        if (ranking == null || ranking.trim().isEmpty() || isChampion()) {
            return 0;
        }
        
        try {
            return Integer.parseInt(ranking.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}