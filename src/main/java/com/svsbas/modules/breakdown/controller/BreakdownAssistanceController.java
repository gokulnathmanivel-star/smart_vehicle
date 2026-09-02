package com.svsbas.modules.breakdown.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.breakdown.dto.BreakdownResponse;
import com.svsbas.modules.breakdown.dto.BreakdownStatusUpdateRequest;
import com.svsbas.modules.breakdown.dto.SosRequest;
import com.svsbas.modules.breakdown.service.BreakdownAssistanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/breakdowns")
@Tag(name = "Breakdown Assistance (SOS)", description = "Emergency roadside assistance and dispatch management")
public class BreakdownAssistanceController {

    private final BreakdownAssistanceService breakdownService;

    public BreakdownAssistanceController(BreakdownAssistanceService breakdownService) {
        this.breakdownService = breakdownService;
    }

    @PostMapping("/sos")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Trigger emergency roadside assistance with GPS coordinates")
    public ResponseEntity<ApiResponse<BreakdownResponse>> requestSos(
            Authentication authentication,
            @Valid @RequestBody SosRequest request) {
        BreakdownResponse response = breakdownService.requestSos(authentication.getName(), request);
        return new ResponseEntity<>(ApiResponse.success("Emergency SOS alert dispatched", response), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MECHANIC')")
    @Operation(summary = "List breakdown tickets (Customer sees own; Mechanic sees assigned; Admin sees all)")
    public ResponseEntity<ApiResponse<List<BreakdownResponse>>> getBreakdowns(Authentication authentication) {
        List<BreakdownResponse> list = breakdownService.getBreakdownsForUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MECHANIC')")
    @Operation(summary = "Get breakdown ticket details by ID")
    public ResponseEntity<ApiResponse<BreakdownResponse>> getBreakdownById(@PathVariable Long id) {
        BreakdownResponse response = breakdownService.getBreakdownById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/dispatch")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dispatch mechanic to breakdown incident (Admin only)")
    public ResponseEntity<ApiResponse<BreakdownResponse>> dispatchMechanic(
            @PathVariable Long id,
            @RequestParam Long mechanicUserId) {
        BreakdownResponse response = breakdownService.dispatchMechanic(id, mechanicUserId);
        return ResponseEntity.ok(ApiResponse.success("Mechanic dispatched", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MECHANIC', 'ADMIN')")
    @Operation(summary = "Update breakdown incident status (Mechanic / Admin)")
    public ResponseEntity<ApiResponse<BreakdownResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BreakdownStatusUpdateRequest request) {
        BreakdownResponse response = breakdownService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }
}
