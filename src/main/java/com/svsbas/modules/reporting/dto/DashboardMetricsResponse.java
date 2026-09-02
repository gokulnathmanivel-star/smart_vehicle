package com.svsbas.modules.reporting.dto;

import java.math.BigDecimal;

public class DashboardMetricsResponse {
    private long totalCustomers;
    private long totalVehicles;
    private long activeBookings;
    private long activeSosRequests;
    private long totalMechanics;
    private long availableMechanics;
    private BigDecimal totalRevenue;

    public DashboardMetricsResponse() {}

    public DashboardMetricsResponse(long totalCustomers, long totalVehicles, long activeBookings, long activeSosRequests, long totalMechanics, long availableMechanics, BigDecimal totalRevenue) {
        this.totalCustomers = totalCustomers;
        this.totalVehicles = totalVehicles;
        this.activeBookings = activeBookings;
        this.activeSosRequests = activeSosRequests;
        this.totalMechanics = totalMechanics;
        this.availableMechanics = availableMechanics;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    public long getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(long totalVehicles) { this.totalVehicles = totalVehicles; }
    public long getActiveBookings() { return activeBookings; }
    public void setActiveBookings(long activeBookings) { this.activeBookings = activeBookings; }
    public long getActiveSosRequests() { return activeSosRequests; }
    public void setActiveSosRequests(long activeSosRequests) { this.activeSosRequests = activeSosRequests; }
    public long getTotalMechanics() { return totalMechanics; }
    public void setTotalMechanics(long totalMechanics) { this.totalMechanics = totalMechanics; }
    public long getAvailableMechanics() { return availableMechanics; }
    public void setAvailableMechanics(long availableMechanics) { this.availableMechanics = availableMechanics; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
