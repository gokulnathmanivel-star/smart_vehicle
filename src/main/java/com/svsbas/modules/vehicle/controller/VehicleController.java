package com.svsbas.modules.vehicle.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.vehicle.dto.VehicleCreateRequest;
import com.svsbas.modules.vehicle.dto.VehicleResponse;
import com.svsbas.modules.vehicle.service.VehicleService;
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
@RequestMapping("/vehicles")
@Tag(name = "Vehicles", description = "Vehicle registration and customer garage management")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Register a new vehicle in customer garage")
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            Authentication authentication,
            @Valid @RequestBody VehicleCreateRequest request) {
        VehicleResponse response = vehicleService.createVehicle(authentication.getName(), request);
        return new ResponseEntity<>(ApiResponse.success("Vehicle registered successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MECHANIC')")
    @Operation(summary = "List vehicles (Customer views own; Admin views all)")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getVehicles(Authentication authentication) {
        List<VehicleResponse> list = vehicleService.getVehiclesForUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MECHANIC')")
    @Operation(summary = "Get vehicle details by ID")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Remove a vehicle from garage")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(
            @PathVariable Long id,
            Authentication authentication) {
        vehicleService.deleteVehicle(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully", null));
    }
}
