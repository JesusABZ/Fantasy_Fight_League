// Crear: src/main/java/com/fantasyfightleague/dto/FightResultDTO.java
package com.fantasyfightleague.dto;

/**
 * DTO para recibir los resultados de una pelea individual
 */
public class FightResultDTO {
    
    private String nombre;
    private String resultado; // "Win" o "Loss"
    private Integer round;
    private String metodo;
    private String tipoDecision; // "Unanimous", "Majority", "Split" (solo para decisiones)
    private Integer golpesTotales;
    private Integer golpesAcertados;
    private Integer golpesSignificantes;
    private Integer takedowns;
    private Integer takedownsAcertados;
    private Integer intentosDeSumision;
    private Integer knockdowns;
    
    // Constructores
    public FightResultDTO() {
    }
    
    // Getters y setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getResultado() {
        return resultado;
    }
    
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    public Integer getRound() {
        return round;
    }
    
    public void setRound(Integer round) {
        this.round = round;
    }
    
    public String getMetodo() {
        return metodo;
    }
    
    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
    
    public String getTipoDecision() {
        return tipoDecision;
    }
    
    public void setTipoDecision(String tipoDecision) {
        this.tipoDecision = tipoDecision;
    }
    
    public Integer getGolpesTotales() {
        return golpesTotales;
    }
    
    public void setGolpesTotales(Integer golpesTotales) {
        this.golpesTotales = golpesTotales;
    }
    
    public Integer getGolpesAcertados() {
        return golpesAcertados;
    }
    
    public void setGolpesAcertados(Integer golpesAcertados) {
        this.golpesAcertados = golpesAcertados;
    }
    
    public Integer getGolpesSignificantes() {
        return golpesSignificantes;
    }
    
    public void setGolpesSignificantes(Integer golpesSignificantes) {
        this.golpesSignificantes = golpesSignificantes;
    }
    
    public Integer getTakedowns() {
        return takedowns;
    }
    
    public void setTakedowns(Integer takedowns) {
        this.takedowns = takedowns;
    }
    
    public Integer getTakedownsAcertados() {
        return takedownsAcertados;
    }
    
    public void setTakedownsAcertados(Integer takedownsAcertados) {
        this.takedownsAcertados = takedownsAcertados;
    }
    
    public Integer getIntentosDeSumision() {
        return intentosDeSumision;
    }
    
    public void setIntentosDeSumision(Integer intentosDeSumision) {
        this.intentosDeSumision = intentosDeSumision;
    }
    
    public Integer getKnockdowns() {
        return knockdowns;
    }
    
    public void setKnockdowns(Integer knockdowns) {
        this.knockdowns = knockdowns;
    }
}