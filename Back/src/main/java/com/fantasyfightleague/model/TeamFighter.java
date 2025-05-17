package com.fantasyfightleague.model;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "team_fighters")
public class TeamFighter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
    
    @ManyToOne
    @JoinColumn(name = "fighter_id", nullable = false)
    private Fighter fighter;
    
    @Column(name = "buy_price")
    private Integer buyPrice;
    
    @Column(name = "selected_for_event")
    private boolean selectedForEvent = false;
    
    @Column(name = "acquired_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acquiredAt;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        acquiredAt = new Date();
    }
    
    // Constructores
    public TeamFighter() {
    }
    
    public TeamFighter(Team team, Fighter fighter, Integer buyPrice) {
        this.team = team;
        this.fighter = fighter;
        this.buyPrice = buyPrice;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public Fighter getFighter() {
        return fighter;
    }
    
    public void setFighter(Fighter fighter) {
        this.fighter = fighter;
    }
    
    public Integer getBuyPrice() {
        return buyPrice;
    }
    
    public void setBuyPrice(Integer buyPrice) {
        this.buyPrice = buyPrice;
    }
    
    public boolean isSelectedForEvent() {
        return selectedForEvent;
    }
    
    public void setSelectedForEvent(boolean selectedForEvent) {
        this.selectedForEvent = selectedForEvent;
    }
    
    public Date getAcquiredAt() {
        return acquiredAt;
    }
    
    public void setAcquiredAt(Date acquiredAt) {
        this.acquiredAt = acquiredAt;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
