package com.svsbas.modules.catalog.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.catalog.dto.ServiceCatalogRequest;
import com.svsbas.modules.catalog.dto.ServiceCatalogResponse;
import com.svsbas.modules.catalog.entity.ServiceCategory;
import com.svsbas.modules.catalog.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
@Tag(name = "Service Catalog", description = "Standard service packages and tariff management")
public class ServiceCatalogController {

    private final ServiceCatalogService catalogService;

    public ServiceCatalogController(ServiceCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    @Operation(summary = "Browse active service catalog items (Public / Authenticated)")
    public ResponseEntity<ApiResponse<List<ServiceCatalogResponse>>> getActiveServices(
            @RequestParam(required = false) ServiceCategory category) {
        List<ServiceCatalogResponse> list = catalogService.getActiveServices(category);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all catalog items including disabled (Admin only)")
    public ResponseEntity<ApiResponse<List<ServiceCatalogResponse>>> getAllServices() {
        List<ServiceCatalogResponse> list = catalogService.getAllServices();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new catalog service package (Admin only)")
    public ResponseEntity<ApiResponse<ServiceCatalogResponse>> createService(
            @Valid @RequestBody ServiceCatalogRequest request) {
        ServiceCatalogResponse response = catalogService.createServiceItem(request);
        return new ResponseEntity<>(ApiResponse.success("Service package created", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update service package details & tariff (Admin only)")
    public ResponseEntity<ApiResponse<ServiceCatalogResponse>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceCatalogRequest request) {
        ServiceCatalogResponse response = catalogService.updateServiceItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Service package updated", response));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle active/disabled status (Admin only)")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(@PathVariable Long id) {
        catalogService.toggleActiveStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Status updated", null));
    }
}
