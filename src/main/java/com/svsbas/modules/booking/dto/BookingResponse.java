package com.svsbas.modules.booking.dto;

import com.svsbas.modules.booking.entity.BookingStatus;
import com.svsbas.modules.booking.entity.ServiceBooking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingResponse {
    private Long id;
    private String bookingRef;
    private Long vehicleId;
    private String vehicleInfo;
    private String regNumber;
    private String customerName;
    private String customerPhone;
    private String mechanicName;
    private Long mechanicId;
    private LocalDateTime preferredSlot;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private BookingStatus status;
    private String customerNotes;
    private String mechanicNotes;
    private BigDecimal estimatedCost;
    private List<String> services;
    private LocalDateTime createdAt;

    public BookingResponse() {}

    public BookingResponse(ServiceBooking b) {
        this.id = b.getId();
        this.bookingRef = b.getBookingRef();
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
        }
        this.preferredSlot = b.getPreferredSlot();
        this.actualStartTime = b.getActualStartTime();
        this.actualEndTime = b.getActualEndTime();
        this.status = b.getStatus();
        this.customerNotes = b.getCustomerNotes();
        this.mechanicNotes = b.getMechanicNotes();
        this.estimatedCost = b.getEstimatedCost();
        this.createdAt = b.getCreatedAt();
        if (b.getServices() != null) {
            this.services = b.getServices().stream()
                    .map(s -> s.getCatalogItem() != null ? s.getCatalogItem().getServiceName() : "Service")
                    .collect(Collectors.toList());
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
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
    public String getMechanicName() { return mechanicName; }
    public void setMechanicName(String mechanicName) { this.mechanicName = mechanicName; }
    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }
    public LocalDateTime getPreferredSlot() { return preferredSlot; }
    public void setPreferredSlot(LocalDateTime preferredSlot) { this.preferredSlot = preferredSlot; }
    public LocalDateTime getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(LocalDateTime actualStartTime) { this.actualStartTime = actualStartTime; }
    public LocalDateTime getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(LocalDateTime actualEndTime) { this.actualEndTime = actualEndTime; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public String getCustomerNotes() { return customerNotes; }
    public void setCustomerNotes(String customerNotes) { this.customerNotes = customerNotes; }
    public String getMechanicNotes() { return mechanicNotes; }
    public void setMechanicNotes(String mechanicNotes) { this.mechanicNotes = mechanicNotes; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
