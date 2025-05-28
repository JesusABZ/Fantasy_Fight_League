// Back/src/main/java/com/fantasyfightleague/model/PasswordResetToken.java
package com.fantasyfightleague.model;

import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    private static final int EXPIRATION = 24; // 24 horas
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String token;
    
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    
    private Date expiryDate;
    
    @Column(name = "used")
    private boolean used = false;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    public PasswordResetToken() {
        this.expiryDate = calculateExpiryDate(EXPIRATION);
        this.createdAt = new Date();
    }
    
    public PasswordResetToken(String token, User user) {
        this();
        this.token = token;
        this.user = user;
    }
    
    private Date calculateExpiryDate(int expiryTimeInHours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, expiryTimeInHours);
        return cal.getTime();
    }
    
    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
    
    public boolean isValidForUse() {
        return !used && !isExpired();
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}