package com.svsbas.modules.breakdown.dto;

import com.svsbas.modules.breakdown.entity.BreakdownType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SosRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Breakdown type is required")
    private BreakdownType breakdownType;

    @NotNull(message = "GPS Latitude is required")
    private Double latitude;

    @NotNull(message = "GPS Longitude is required")
    private Double longitude;

    @NotBlank(message = "Location address / landmark is required")
    private String locationAddress;

    public SosRequest() {}

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public BreakdownType getBreakdownType() { return breakdownType; }
    public void setBreakdownType(BreakdownType breakdownType) { this.breakdownType = breakdownType; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String locationAddress) { this.locationAddress = locationAddress; }
}
