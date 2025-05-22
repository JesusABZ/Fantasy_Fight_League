package com.fantasyfightleague.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    // MANTENER date para compatibilidad con datos existentes
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    // NUEVAS FECHAS
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP) 
    private Date startDate; // Fecha y hora inicio del evento
    
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate; // Fecha y hora fin del evento
    
    @Column(name = "picks_deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date picksDeadline; // 24h antes del inicio (calculado automáticamente)
    
    private String location;
    
    private String status; // "UPCOMING", "LIVE", "COMPLETED", "CANCELLED"
    
    private String description;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    // NUEVA RELACIÓN: Luchadores que participan en este evento
    @ManyToMany
    @JoinTable(
        name = "event_fighters",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "fighter_id")
    )
    private Set<Fighter> fighters = new HashSet<>();
    
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
        
        // Calcular automáticamente picks_deadline (24h antes del inicio)
        if (startDate != null && picksDeadline == null) {
            picksDeadline = new Date(startDate.getTime() - (24 * 60 * 60 * 1000)); // 24h antes
        }
        
        // Status por defecto
        if (status == null) {
            status = "UPCOMING";
        }
        
        // Si no hay startDate pero sí date, migrar automáticamente
        if (startDate == null && date != null) {
            startDate = date;
            picksDeadline = new Date(startDate.getTime() - (24 * 60 * 60 * 1000));
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
        
        // Recalcular picks_deadline si cambió startDate
        if (startDate != null) {
            picksDeadline = new Date(startDate.getTime() - (24 * 60 * 60 * 1000));
        }
    }
    
    // Constructores
    public Event() {
    }
    
    public Event(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Métodos de utilidad
    public boolean isPicksOpen() {
        Date now = new Date();
        return picksDeadline != null && now.before(picksDeadline) && "UPCOMING".equals(status);
    }
    
    public boolean isLive() {
        return "LIVE".equals(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public void addFighter(Fighter fighter) {
        fighters.add(fighter);
    }
    
    public void removeFighter(Fighter fighter) {
        fighters.remove(fighter);
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
    
    // MANTENER getDate() para compatibilidad
    public Date getDate() {
        return startDate != null ? startDate : date;
    }
    
    public void setDate(Date date) {
        this.date = date;
        // Si no hay startDate, usar date como startDate
        if (startDate == null) {
            this.startDate = date;
        }
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        // Recalcular deadline automáticamente
        if (startDate != null) {
            this.picksDeadline = new Date(startDate.getTime() - (24 * 60 * 60 * 1000));
        }
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Date getPicksDeadline() {
        return picksDeadline;
    }
    
    public void setPicksDeadline(Date picksDeadline) {
        this.picksDeadline = picksDeadline;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Set<Fighter> getFighters() {
        return fighters;
    }
    
    public void setFighters(Set<Fighter> fighters) {
        this.fighters = fighters;
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