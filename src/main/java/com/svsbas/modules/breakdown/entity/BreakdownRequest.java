package com.svsbas.modules.breakdown.entity;

import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "breakdown_requests")
public class BreakdownRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sos_ref", nullable = false, unique = true, length = 36)
    private String sosRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mechanic_id")
    private User mechanic;

    @Enumerated(EnumType.STRING)
    @Column(name = "breakdown_type", nullable = false, length = 30)
    private BreakdownType breakdownType;

    @Column(name = "customer_latitude", nullable = false)
    private Double customerLatitude;

    @Column(name = "customer_longitude", nullable = false)
    private Double customerLongitude;

    @Column(name = "location_address", nullable = false, columnDefinition = "TEXT")
    private String locationAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BreakdownStatus status = BreakdownStatus.DISPATCHED;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "initial_assessment", columnDefinition = "TEXT")
    private String initialAssessment;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BreakdownRequest() {}

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
    public String getSosRef() { return sosRef; }
    public void setSosRef(String sosRef) { this.sosRef = sosRef; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    public User getMechanic() { return mechanic; }
    public void setMechanic(User mechanic) { this.mechanic = mechanic; }
    public BreakdownType getBreakdownType() { return breakdownType; }
    public void setBreakdownType(BreakdownType breakdownType) { this.breakdownType = breakdownType; }
    public Double getCustomerLatitude() { return customerLatitude; }
    public void setCustomerLatitude(Double customerLatitude) { this.customerLatitude = customerLatitude; }
    public Double getCustomerLongitude() { return customerLongitude; }
    public void setCustomerLongitude(Double customerLongitude) { this.customerLongitude = customerLongitude; }
    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String locationAddress) { this.locationAddress = locationAddress; }
    public BreakdownStatus getStatus() { return status; }
    public void setStatus(BreakdownStatus status) { this.status = status; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    public LocalDateTime getArrivedAt() { return arrivedAt; }
    public void setArrivedAt(LocalDateTime arrivedAt) { this.arrivedAt = arrivedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public String getInitialAssessment() { return initialAssessment; }
    public void setInitialAssessment(String initialAssessment) { this.initialAssessment = initialAssessment; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
