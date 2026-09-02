package com.svsbas.modules.mechanic.dto;

import com.svsbas.modules.mechanic.entity.MechanicProfile;
import com.svsbas.modules.mechanic.entity.MechanicStatus;

import java.math.BigDecimal;

public class MechanicProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private Double latitude;
    private Double longitude;
    private boolean available;
    private MechanicStatus currentStatus;
    private BigDecimal rating;

    public MechanicProfileResponse() {}

    public MechanicProfileResponse(MechanicProfile profile) {
        this.id = profile.getId();
        if (profile.getUser() != null) {
            this.userId = profile.getUser().getId();
            this.fullName = profile.getUser().getFullName();
            this.email = profile.getUser().getEmail();
            this.phone = profile.getUser().getPhone();
        }
        this.specialization = profile.getSpecialization();
        this.latitude = profile.getLatitude();
        this.longitude = profile.getLongitude();
        this.available = profile.isAvailable();
        this.currentStatus = profile.getCurrentStatus();
        this.rating = profile.getRating();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public MechanicStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(MechanicStatus currentStatus) { this.currentStatus = currentStatus; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
}
