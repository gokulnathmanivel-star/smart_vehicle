package com.svsbas.modules.mechanic.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.mechanic.dto.MechanicProfileResponse;
import com.svsbas.modules.mechanic.entity.MechanicStatus;
import com.svsbas.modules.mechanic.service.MechanicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mechanics")
@Tag(name = "Mechanics", description = "Mechanic operations, roster, and status management")
public class MechanicController {

    private final MechanicService mechanicService;

    public MechanicController(MechanicService mechanicService) {
        this.mechanicService = mechanicService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Operation(summary = "List mechanic roster")
    public ResponseEntity<ApiResponse<List<MechanicProfileResponse>>> getMechanics(
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        List<MechanicProfileResponse> list = mechanicService.getAllMechanics(availableOnly);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Provision and register a new certified mechanic")
    public ResponseEntity<ApiResponse<MechanicProfileResponse>> createMechanic(
            @org.springframework.web.bind.annotation.RequestBody com.svsbas.modules.mechanic.dto.MechanicCreateRequest request) {
        MechanicProfileResponse response = mechanicService.registerMechanic(request);
        return new ResponseEntity<>(ApiResponse.success("Mechanic registered and credentials issued", response), org.springframework.http.HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('MECHANIC')")
    @Operation(summary = "Get current authenticated mechanic profile")
    public ResponseEntity<ApiResponse<MechanicProfileResponse>> getMyProfile(Authentication authentication) {
        MechanicProfileResponse response = mechanicService.getProfileByEmail(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('MECHANIC')")
    @Operation(summary = "Toggle availability status (IDLE / OFFLINE)")
    public ResponseEntity<ApiResponse<MechanicProfileResponse>> updateStatus(
            Authentication authentication,
            @RequestParam MechanicStatus status) {
        MechanicProfileResponse response = mechanicService.updateStatus(authentication.getName(), status);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    @PatchMapping("/telemetry")
    @PreAuthorize("hasRole('MECHANIC')")
    @Operation(summary = "Push mechanic GPS coordinates")
    public ResponseEntity<ApiResponse<MechanicProfileResponse>> updateLocation(
            Authentication authentication,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        MechanicProfileResponse response = mechanicService.updateLocation(authentication.getName(), latitude, longitude);
        return ResponseEntity.ok(ApiResponse.success("Coordinates updated", response));
    }
}
