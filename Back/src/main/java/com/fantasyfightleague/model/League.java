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
    
    // Para ligas públicas: referencia al evento específico
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;  // Obligatorio para ligas públicas, null para privadas
    
    // Para ligas privadas: código de invitación
    @Column(name = "invitation_code", unique = true)
    private String invitationCode;  // Solo para ligas privadas
    
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "active")
    private boolean active = true;
    
    // Para ligas públicas: fecha de eliminación automática
    @Column(name = "auto_delete_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date autoDeleteDate;
    
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
    
    public Date getAutoDeleteDate() {
        return autoDeleteDate;
    }
    
    public void setAutoDeleteDate(Date autoDeleteDate) {
        this.autoDeleteDate = autoDeleteDate;
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
    
    public int getMemberCount() {
        return members.size();
    }
}