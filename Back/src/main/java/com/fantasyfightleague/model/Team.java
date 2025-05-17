package com.fantasyfightleague.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @Column(name = "current_budget")
    private Integer currentBudget;
    
    @Column(name = "event_points")
    private Integer eventPoints = 0;
    
    @Column(name = "total_points")
    private Integer totalPoints = 0;
    
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamFighter> teamFighters = new HashSet<>();
    
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
    public Team() {
    }
    
    public Team(String name, User user, League league) {
        this.name = name;
        this.user = user;
        this.league = league;
        this.currentBudget = league.getInitialBudget();
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public League getLeague() {
        return league;
    }
    
    public void setLeague(League league) {
        this.league = league;
    }
    
    public Integer getCurrentBudget() {
        return currentBudget;
    }
    
    public void setCurrentBudget(Integer currentBudget) {
        this.currentBudget = currentBudget;
    }
    
    public Integer getEventPoints() {
        return eventPoints;
    }
    
    public void setEventPoints(Integer eventPoints) {
        this.eventPoints = eventPoints;
    }
    
    public Integer getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    public Set<TeamFighter> getTeamFighters() {
        return teamFighters;
    }
    
    public void setTeamFighters(Set<TeamFighter> teamFighters) {
        this.teamFighters = teamFighters;
    }
    
    public void addTeamFighter(TeamFighter teamFighter) {
        teamFighters.add(teamFighter);
        teamFighter.setTeam(this);
    }
    
    public void removeTeamFighter(TeamFighter teamFighter) {
        teamFighters.remove(teamFighter);
        teamFighter.setTeam(null);
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