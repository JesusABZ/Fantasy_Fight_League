package com.fantasyfightleague.dto;

/**
 * DTO para la importación completa de luchadores con todos sus datos
 */
public class CompleteFighterDTO {
    
    // Datos básicos del luchador
    private String name;
    private String record;
    private String nationality;
    private String weightClass;
    
    // Imagen
    private String imageUrl;
    
    // Datos para el cálculo de precio
    private Integer price; // Precio directo (opcional)
    private String position; // "MAIN_EVENT", "CO_MAIN", "MAIN_CARD", "PRELIMS", "EARLY_PRELIMS"
    private Boolean isFavorite;
    private String ranking; // "C" para campeón, números del "1" al "15" para rankings, o null/vacío
    
    // Constructores
    public CompleteFighterDTO() {
    }
    
    // Getters y setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRecord() {
        return record;
    }
    
    public void setRecord(String record) {
        this.record = record;
    }
    
    public String getNationality() {
        return nationality;
    }
    
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    
    public String getWeightClass() {
        return weightClass;
    }
    
    public void setWeightClass(String weightClass) {
        this.weightClass = weightClass;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
}