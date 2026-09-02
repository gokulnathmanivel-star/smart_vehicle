package com.svsbas.modules.booking.dto;

import com.svsbas.modules.booking.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;

public class BookingStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private BookingStatus status;

    private String mechanicNotes;

    public BookingStatusUpdateRequest() {}

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public String getMechanicNotes() { return mechanicNotes; }
    public void setMechanicNotes(String mechanicNotes) { this.mechanicNotes = mechanicNotes; }
}
