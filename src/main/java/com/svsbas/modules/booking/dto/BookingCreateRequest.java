package com.svsbas.modules.booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class BookingCreateRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotEmpty(message = "At least one service package must be selected")
    private List<Long> serviceCatalogIds;

    @NotNull(message = "Preferred slot date and time is required")
    private LocalDateTime preferredSlot;

    private String customerNotes;

    public BookingCreateRequest() {}

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public List<Long> getServiceCatalogIds() { return serviceCatalogIds; }
    public void setServiceCatalogIds(List<Long> serviceCatalogIds) { this.serviceCatalogIds = serviceCatalogIds; }
    public LocalDateTime getPreferredSlot() { return preferredSlot; }
    public void setPreferredSlot(LocalDateTime preferredSlot) { this.preferredSlot = preferredSlot; }
    public String getCustomerNotes() { return customerNotes; }
    public void setCustomerNotes(String customerNotes) { this.customerNotes = customerNotes; }
}
