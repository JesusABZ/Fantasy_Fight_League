package com.fantasyfightleague.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "picks")
public class Pick {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    // Luchadores elegidos (1-3)
    @ManyToMany
    @JoinTable(
        name = "pick_fighters",
        joinColumns = @JoinColumn(name = "pick_id"),
        inverseJoinColumns = @JoinColumn(name = "fighter_id")
    )
    private Set<Fighter> selectedFighters = new HashSet<>();
    
    @Column(name = "total_cost")
    private Integer totalCost; // Costo total de los luchadores elegidos
    
    @Column(name = "remaining_budget")
    private Integer remainingBudget; // Presupuesto restante después del pick
    
    @Column(name = "event_points")
    private Integer eventPoints = 0; // Puntos obtenidos en este evento
    
    @Column(name = "is_locked")
    private boolean isLocked = false; // Se bloquea cuando pasa el deadline o inicia el evento
    
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
    public Pick() {
    }
    
    public Pick(User user, League league, Event event) {
        this.user = user;
        this.league = league;
        this.event = event;
        this.remainingBudget = league.getInitialBudget(); // Presupuesto inicial de la liga
    }
    
    // Métodos de utilidad
    public void addFighter(Fighter fighter) {
        selectedFighters.add(fighter);
        calculateTotalCost();
    }
    
    public void removeFighter(Fighter fighter) {
        selectedFighters.remove(fighter);
        calculateTotalCost();
    }
    
    public void calculateTotalCost() {
        totalCost = selectedFighters.stream()
                .mapToInt(Fighter::getPrice)
                .sum();
        remainingBudget = league.getInitialBudget() - totalCost;
    }
    
    public boolean isValidPick() {
        int fighterCount = selectedFighters.size();
        return fighterCount >= 1 && fighterCount <= 3 && totalCost <= league.getInitialBudget();
    }
    
    public boolean canBeModified() {
        if (isLocked) return false;
        if (event.getPicksDeadline() == null) return true;
        return new Date().before(event.getPicksDeadline());
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public Set<Fighter> getSelectedFighters() {
        return selectedFighters;
    }
    
    public void setSelectedFighters(Set<Fighter> selectedFighters) {
        this.selectedFighters = selectedFighters;
        calculateTotalCost();
    }
    
    public Integer getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }
    
    public Integer getRemainingBudget() {
        return remainingBudget;
    }
    
    public void setRemainingBudget(Integer remainingBudget) {
        this.remainingBudget = remainingBudget;
    }
    
    public Integer getEventPoints() {
        return eventPoints;
    }
    
    public void setEventPoints(Integer eventPoints) {
        this.eventPoints = eventPoints;
    }
    
    public boolean isLocked() {
        return isLocked;
    }
    
    public void setLocked(boolean locked) {
        isLocked = locked;
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