package com.svsbas.modules.booking.dto;

import jakarta.validation.constraints.NotNull;

public class AssignMechanicRequest {
    @NotNull(message = "Mechanic user ID is required")
    private Long mechanicUserId;

    public AssignMechanicRequest() {}
    public AssignMechanicRequest(Long mechanicUserId) {
        this.mechanicUserId = mechanicUserId;
    }

    public Long getMechanicUserId() { return mechanicUserId; }
    public void setMechanicUserId(Long mechanicUserId) { this.mechanicUserId = mechanicUserId; }
}
