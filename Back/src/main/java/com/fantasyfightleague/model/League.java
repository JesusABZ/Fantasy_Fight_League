package com.fantasyfightleague.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "leagues")
public class League {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private String type;  // "PUBLIC" o "PRIVATE"
    
    @Column(name = "initial_budget", nullable = false)
    private Integer initialBudget = 100;
    
    @Column(name = "max_fighters")
    private Integer maxFighters = 5;
    
    @Column(name = "min_fighters_event")
    private Integer minFightersEvent = 1;
    
    @Column(name = "max_fighters_event")
    private Integer maxFightersEvent = 3;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;  // Solo para ligas públicas
    
    @Column(name = "invitation_code")
    private String invitationCode;  // Solo para ligas privadas
    
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    private boolean active = true;
    
    @ManyToMany
    @JoinTable(
        name = "league_members",
        joinColumns = @JoinColumn(name = "league_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
    
    // Constructores
    public League() {
    }
    
    public League(String name, String type, User creator) {
        this.name = name;
        this.type = type;
        this.creator = creator;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getInitialBudget() {
        return initialBudget;
    }
    
    public void setInitialBudget(Integer initialBudget) {
        this.initialBudget = initialBudget;
    }
    
    public Integer getMaxFighters() {
        return maxFighters;
    }
    
    public void setMaxFighters(Integer maxFighters) {
        this.maxFighters = maxFighters;
    }
    
    public Integer getMinFightersEvent() {
        return minFightersEvent;
    }
    
    public void setMinFightersEvent(Integer minFightersEvent) {
        this.minFightersEvent = minFightersEvent;
    }
    
    public Integer getMaxFightersEvent() {
        return maxFightersEvent;
    }
    
    public void setMaxFightersEvent(Integer maxFightersEvent) {
        this.maxFightersEvent = maxFightersEvent;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public String getInvitationCode() {
        return invitationCode;
    }
    
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
    
    public User getCreator() {
        return creator;
    }
    
    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Set<User> getMembers() {
        return members;
    }
    
    public void setMembers(Set<User> members) {
        this.members = members;
    }
    
    public void addMember(User user) {
        this.members.add(user);
    }
    
    public void removeMember(User user) {
        this.members.remove(user);
    }
}