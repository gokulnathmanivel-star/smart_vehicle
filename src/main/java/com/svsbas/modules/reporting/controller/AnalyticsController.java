package com.svsbas.modules.reporting.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.reporting.dto.DashboardMetricsResponse;
import com.svsbas.modules.reporting.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Reporting & Analytics", description = "Operational KPIs, utilization, and revenue statistics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get central dispatch dashboard KPIs and revenue metrics (Admin only)")
    public ResponseEntity<ApiResponse<DashboardMetricsResponse>> getDashboardMetrics() {
        DashboardMetricsResponse response = analyticsService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
