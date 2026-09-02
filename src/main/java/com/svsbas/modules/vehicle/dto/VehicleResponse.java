package com.svsbas.modules.vehicle.dto;

import com.svsbas.modules.vehicle.entity.FuelType;
import com.svsbas.modules.vehicle.entity.Vehicle;

import java.time.LocalDateTime;

public class VehicleResponse {
    private Long id;
    private Long userId;
    private String ownerName;
    private String regNumber;
    private String brand;
    private String model;
    private FuelType fuelType;
    private Integer manufactureYear;
    private Integer currentMileage;
    private LocalDateTime createdAt;

    public VehicleResponse() {}

    public VehicleResponse(Vehicle vehicle) {
        this.id = vehicle.getId();
        if (vehicle.getUser() != null) {
            this.userId = vehicle.getUser().getId();
            this.ownerName = vehicle.getUser().getFullName();
        }
        this.regNumber = vehicle.getRegNumber();
        this.brand = vehicle.getBrand();
        this.model = vehicle.getModel();
        this.fuelType = vehicle.getFuelType();
        this.manufactureYear = vehicle.getManufactureYear();
        this.currentMileage = vehicle.getCurrentMileage();
        this.createdAt = vehicle.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
    public Integer getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(Integer manufactureYear) { this.manufactureYear = manufactureYear; }
    public Integer getCurrentMileage() { return currentMileage; }
    public void setCurrentMileage(Integer currentMileage) { this.currentMileage = currentMileage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
