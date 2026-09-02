package com.svsbas.modules.breakdown.dto;

import com.svsbas.modules.breakdown.entity.BreakdownStatus;
import jakarta.validation.constraints.NotNull;

public class BreakdownStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private BreakdownStatus status;

    private String assessment;
    private String resolutionNotes;

    public BreakdownStatusUpdateRequest() {}

    public BreakdownStatus getStatus() { return status; }
    public void setStatus(BreakdownStatus status) { this.status = status; }
    public String getAssessment() { return assessment; }
    public void setAssessment(String assessment) { this.assessment = assessment; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}
