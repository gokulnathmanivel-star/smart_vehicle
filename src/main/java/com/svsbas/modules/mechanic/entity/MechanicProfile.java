package com.svsbas.modules.mechanic.entity;

import com.svsbas.modules.user.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mechanic_profiles")
public class MechanicProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 150)
    private String specialization;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 20)
    private MechanicStatus currentStatus = MechanicStatus.IDLE;

    @Column(precision = 3, scale = 2, nullable = false)
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MechanicProfile() {}

    public MechanicProfile(User user, String specialization, Double latitude, Double longitude) {
        this.user = user;
        this.specialization = specialization;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isAvailable = true;
        this.currentStatus = MechanicStatus.IDLE;
        this.rating = BigDecimal.valueOf(5.0);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public MechanicStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(MechanicStatus currentStatus) { this.currentStatus = currentStatus; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
