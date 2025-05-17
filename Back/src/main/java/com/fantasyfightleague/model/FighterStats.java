package com.fantasyfightleague.model;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "fighter_stats")
public class FighterStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "fighter_id", nullable = false)
    private Fighter fighter;
    
    @ManyToOne
    @JoinColumn(name = "fight_id", nullable = false)
    private Fight fight;
    
    @Column(name = "minutes_fought")
    private Integer minutesFought;
    
    private Integer knockdowns;
    
    private Integer takedowns;
    
    private Integer submissions;
    
    @Column(name = "significant_strikes")
    private Integer significantStrikes;
    
    @Column(name = "total_strikes")
    private Integer totalStrikes;
    
    private Integer points;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
    
    // Constructores
    public FighterStats() {
    }
    
    public FighterStats(Fighter fighter, Fight fight) {
        this.fighter = fighter;
        this.fight = fight;
        this.points = 0;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Fighter getFighter() {
        return fighter;
    }
    
    public void setFighter(Fighter fighter) {
        this.fighter = fighter;
    }
    
    public Fight getFight() {
        return fight;
    }
    
    public void setFight(Fight fight) {
        this.fight = fight;
    }
    
    public Integer getMinutesFought() {
        return minutesFought;
    }
    
    public void setMinutesFought(Integer minutesFought) {
        this.minutesFought = minutesFought;
    }
    
    public Integer getKnockdowns() {
        return knockdowns;
    }
    
    public void setKnockdowns(Integer knockdowns) {
        this.knockdowns = knockdowns;
    }
    
    public Integer getTakedowns() {
        return takedowns;
    }
    
    public void setTakedowns(Integer takedowns) {
        this.takedowns = takedowns;
    }
    
    public Integer getSubmissions() {
        return submissions;
    }
    
    public void setSubmissions(Integer submissions) {
        this.submissions = submissions;
    }
    
    public Integer getSignificantStrikes() {
        return significantStrikes;
    }
    
    public void setSignificantStrikes(Integer significantStrikes) {
        this.significantStrikes = significantStrikes;
    }
    
    public Integer getTotalStrikes() {
        return totalStrikes;
    }
    
    public void setTotalStrikes(Integer totalStrikes) {
        this.totalStrikes = totalStrikes;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}