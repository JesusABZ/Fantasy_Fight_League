package com.fantasyfightleague.dto;

/**
 * DTO para la importación simplificada de luchadores
 */
public class FighterDTO {
    
    private String name;
    private String record;
    private String nationality;
    private String weightClass;
    
    // Constructores
    public FighterDTO() {
    }
    
    public FighterDTO(String name, String record, String nationality, String weightClass) {
        this.name = name;
        this.record = record;
        this.nationality = nationality;
        this.weightClass = weightClass;
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
}