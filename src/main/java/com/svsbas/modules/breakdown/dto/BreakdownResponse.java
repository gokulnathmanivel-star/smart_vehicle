package com.svsbas.modules.breakdown.dto;

import com.svsbas.modules.breakdown.entity.BreakdownRequest;
import com.svsbas.modules.breakdown.entity.BreakdownStatus;
import com.svsbas.modules.breakdown.entity.BreakdownType;

import java.time.LocalDateTime;

public class BreakdownResponse {
    private Long id;
    private String sosRef;
    private Long vehicleId;
    private String vehicleInfo;
    private String regNumber;
    private String customerName;
    private String customerPhone;
    private Long mechanicId;
    private String mechanicName;
    private String mechanicPhone;
    private BreakdownType breakdownType;
    private Double customerLatitude;
    private Double customerLongitude;
    private String locationAddress;
    private BreakdownStatus status;
    private Double distanceKm;
    private Integer etaMinutes;
    private String initialAssessment;
    private String resolutionNotes;
    private LocalDateTime createdAt;

    public BreakdownResponse() {}

    public BreakdownResponse(BreakdownRequest b) {
        this.id = b.getId();
        this.sosRef = b.getSosRef();
        if (b.getVehicle() != null) {
            this.vehicleId = b.getVehicle().getId();
            this.vehicleInfo = b.getVehicle().getBrand() + " " + b.getVehicle().getModel();
            this.regNumber = b.getVehicle().getRegNumber();
        }
        if (b.getCustomer() != null) {
            this.customerName = b.getCustomer().getFullName();
            this.customerPhone = b.getCustomer().getPhone();
        }
        if (b.getMechanic() != null) {
            this.mechanicId = b.getMechanic().getId();
            this.mechanicName = b.getMechanic().getFullName();
            this.mechanicPhone = b.getMechanic().getPhone();
        }
        this.breakdownType = b.getBreakdownType();
        this.customerLatitude = b.getCustomerLatitude();
        this.customerLongitude = b.getCustomerLongitude();
        this.locationAddress = b.getLocationAddress();
        this.status = b.getStatus();
        this.initialAssessment = b.getInitialAssessment();
        this.resolutionNotes = b.getResolutionNotes();
        this.createdAt = b.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSosRef() { return sosRef; }
    public void setSosRef(String sosRef) { this.sosRef = sosRef; }
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }
    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }
    public String getMechanicName() { return mechanicName; }
    public void setMechanicName(String mechanicName) { this.mechanicName = mechanicName; }
    public String getMechanicPhone() { return mechanicPhone; }
    public void setMechanicPhone(String mechanicPhone) { this.mechanicPhone = mechanicPhone; }
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
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public Integer getEtaMinutes() { return etaMinutes; }
    public void setEtaMinutes(Integer etaMinutes) { this.etaMinutes = etaMinutes; }
    public String getInitialAssessment() { return initialAssessment; }
    public void setInitialAssessment(String initialAssessment) { this.initialAssessment = initialAssessment; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
