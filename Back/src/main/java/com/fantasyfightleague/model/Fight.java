package com.fantasyfightleague.model;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "fights")
public class Fight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "fighter1_id", nullable = false)
    private Fighter fighter1;
    
    @ManyToOne
    @JoinColumn(name = "fighter2_id", nullable = false)
    private Fighter fighter2;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    
    @Column(name = "is_main_event")
    private boolean isMainEvent = false;
    
    @Column(name = "is_title_fight")
    private boolean isTitleFight = false;
    
    @Column(name = "weight_class")
    private String weightClass;
    
    @Column(name = "number_of_rounds")
    private Integer numberOfRounds = 3;
    
    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Fighter winner;
    
    // Método de victoria (KO, Sumisión, Decisión)
    @Column(name = "victory_method")
    private String victoryMethod;
    
    // Ronda en la que terminó la pelea
    @Column(name = "end_round")
    private Integer endRound;
    
    // Tiempo de la ronda en que terminó la pelea (en segundos)
    @Column(name = "end_time")
    private Integer endTime;
    
    @Column(name = "status")
    private String status; // "SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    // Constructores
    public Fight() {
    }
    
    public Fight(Fighter fighter1, Fighter fighter2, Event event) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.event = event;
        this.weightClass = fighter1.getWeightClass(); // Asumimos misma categoría
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Fighter getFighter1() {
        return fighter1;
    }
    
    public void setFighter1(Fighter fighter1) {
        this.fighter1 = fighter1;
    }
    
    public Fighter getFighter2() {
        return fighter2;
    }
    
    public void setFighter2(Fighter fighter2) {
        this.fighter2 = fighter2;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public boolean isMainEvent() {
        return isMainEvent;
    }
    
    public void setMainEvent(boolean mainEvent) {
        isMainEvent = mainEvent;
    }
    
    public boolean isTitleFight() {
        return isTitleFight;
    }
    
    public void setTitleFight(boolean titleFight) {
        isTitleFight = titleFight;
    }
    
    public String getWeightClass() {
        return weightClass;
    }
    
    public void setWeightClass(String weightClass) {
        this.weightClass = weightClass;
    }
    
    public Integer getNumberOfRounds() {
        return numberOfRounds;
    }
    
    public void setNumberOfRounds(Integer numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }
    
    public Fighter getWinner() {
        return winner;
    }
    
    public void setWinner(Fighter winner) {
        this.winner = winner;
    }
    
    public String getVictoryMethod() {
        return victoryMethod;
    }
    
    public void setVictoryMethod(String victoryMethod) {
        this.victoryMethod = victoryMethod;
    }
    
    public Integer getEndRound() {
        return endRound;
    }
    
    public void setEndRound(Integer endRound) {
        this.endRound = endRound;
    }
    
    public Integer getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}