// Crear: src/main/java/com/fantasyfightleague/dto/EventResultsDTO.java
package com.fantasyfightleague.dto;

import java.util.List;

/**
 * DTO para recibir los resultados completos de un evento
 */
public class EventResultsDTO {
    
    private String nombreEvento;
    private List<FightResultDTO> resultados;
    
    // Constructores
    public EventResultsDTO() {
    }
    
    public EventResultsDTO(String nombreEvento, List<FightResultDTO> resultados) {
        this.nombreEvento = nombreEvento;
        this.resultados = resultados;
    }
    
    // Getters y setters
    public String getNombreEvento() {
        return nombreEvento;
    }
    
    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }
    
    public List<FightResultDTO> getResultados() {
        return resultados;
    }
    
    public void setResultados(List<FightResultDTO> resultados) {
        this.resultados = resultados;
    }
}