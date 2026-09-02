package com.svsbas.modules.vehicle.dto;

import com.svsbas.modules.vehicle.entity.FuelType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehicleCreateRequest {

    @NotBlank(message = "Registration number is required")
    private String regNumber;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;

    @NotNull(message = "Manufacture year is required")
    @Min(value = 1990, message = "Manufacture year must be 1990 or newer")
    private Integer manufactureYear;

    @NotNull(message = "Current mileage is required")
    @Min(value = 0, message = "Mileage cannot be negative")
    private Integer currentMileage;

    public VehicleCreateRequest() {}

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
}
