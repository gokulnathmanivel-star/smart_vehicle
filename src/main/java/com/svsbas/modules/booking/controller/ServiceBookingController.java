package com.svsbas.modules.booking.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.booking.dto.AssignMechanicRequest;
import com.svsbas.modules.booking.dto.BookingCreateRequest;
import com.svsbas.modules.booking.dto.BookingResponse;
import com.svsbas.modules.booking.dto.BookingStatusUpdateRequest;
import com.svsbas.modules.booking.service.ServiceBookingService;
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
@RequestMapping("/api/v1/bookings")
@Tag(name = "Service Bookings", description = "Regular workshop service slot booking and lifecycle management")
public class ServiceBookingController {

    private final ServiceBookingService bookingService;

    public ServiceBookingController(ServiceBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create regular vehicle maintenance booking")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            Authentication authentication,
            @Valid @RequestBody BookingCreateRequest request) {
        BookingResponse response = bookingService.createBooking(authentication.getName(), request);
        return new ResponseEntity<>(ApiResponse.success("Service booked successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MECHANIC')")
    @Operation(summary = "List bookings (Customer views own; Mechanic views assigned; Admin views all)")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookings(Authentication authentication) {
        List<BookingResponse> list = bookingService.getBookingsForUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MECHANIC')")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign mechanic to booking (Admin only)")
    public ResponseEntity<ApiResponse<BookingResponse>> assignMechanic(
            @PathVariable Long id,
            @Valid @RequestBody AssignMechanicRequest request) {
        BookingResponse response = bookingService.assignMechanic(id, request);
        return ResponseEntity.ok(ApiResponse.success("Mechanic assigned successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MECHANIC', 'ADMIN')")
    @Operation(summary = "Update booking status (Mechanic / Admin)")
    public ResponseEntity<ApiResponse<BookingResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdateRequest request) {
        BookingResponse response = bookingService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Cancel service booking")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {
        BookingResponse response = bookingService.cancelBooking(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled", response));
    }
}
