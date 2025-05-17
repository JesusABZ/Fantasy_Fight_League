package com.fantasyfightleague.model;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "market_listings")
public class MarketListing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;
    
    @ManyToOne
    @JoinColumn(name = "fighter_id", nullable = false)
    private Fighter fighter;
    
    @Column(name = "initial_price", nullable = false)
    private Integer initialPrice;
    
    @Column(name = "current_price")
    private Integer currentPrice;
    
    @Column(name = "listing_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date listingDate;
    
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Column(name = "status", nullable = false)
    private String status; // "ACTIVE", "SOLD", "EXPIRED", "CANCELLED"
    
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
        listingDate = new Date();
        currentPrice = initialPrice;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    // Constructores
    public MarketListing() {
    }
    
    public MarketListing(League league, Fighter fighter, Integer initialPrice, Date endDate) {
        this.league = league;
        this.fighter = fighter;
        this.initialPrice = initialPrice;
        this.currentPrice = initialPrice;
        this.endDate = endDate;
        this.status = "ACTIVE";
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public League getLeague() {
        return league;
    }
    
    public void setLeague(League league) {
        this.league = league;
    }
    
    public Fighter getFighter() {
        return fighter;
    }
    
    public void setFighter(Fighter fighter) {
        this.fighter = fighter;
    }
    
    public Integer getInitialPrice() {
        return initialPrice;
    }
    
    public void setInitialPrice(Integer initialPrice) {
        this.initialPrice = initialPrice;
    }
    
    public Integer getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(Integer currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public Date getListingDate() {
        return listingDate;
    }
    
    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
