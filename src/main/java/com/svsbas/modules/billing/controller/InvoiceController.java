package com.svsbas.modules.billing.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.billing.dto.AddInvoiceItemRequest;
import com.svsbas.modules.billing.dto.InvoiceResponse;
import com.svsbas.modules.billing.dto.PaymentProcessRequest;
import com.svsbas.modules.billing.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Billing & Invoices", description = "Dynamic cost calculation, invoicing, and PDF downloads")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('MECHANIC', 'ADMIN')")
    @Operation(summary = "Generate draft invoice for service booking")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateForBooking(
            @PathVariable Long bookingId,
            @RequestParam(required = false) BigDecimal laborHours,
            @RequestParam(required = false) BigDecimal hourlyRate) {
        InvoiceResponse response = invoiceService.generateForBooking(bookingId, laborHours, hourlyRate);
        return new ResponseEntity<>(ApiResponse.success("Invoice generated", response), HttpStatus.CREATED);
    }

    @PostMapping("/breakdown/{breakdownId}")
    @PreAuthorize("hasAnyRole('MECHANIC', 'ADMIN')")
    @Operation(summary = "Generate invoice for breakdown assistance")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateForBreakdown(
            @PathVariable Long breakdownId,
            @RequestParam(required = false) BigDecimal roadsideFee) {
        InvoiceResponse response = invoiceService.generateForBreakdown(breakdownId, roadsideFee);
        return new ResponseEntity<>(ApiResponse.success("Invoice generated", response), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('MECHANIC', 'ADMIN')")
    @Operation(summary = "Add replacement parts or labor hours to invoice")
    public ResponseEntity<ApiResponse<InvoiceResponse>> addItem(
            @PathVariable Long id,
            @Valid @RequestBody AddInvoiceItemRequest request) {
        InvoiceResponse response = invoiceService.addItem(id, request.getItemType(), request.getDescription(), request.getQuantity(), request.getUnitPrice());
        return ResponseEntity.ok(ApiResponse.success("Item added to invoice", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MECHANIC')")
    @Operation(summary = "Get invoice details and itemized breakdown by ID")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable Long id) {
        InvoiceResponse response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Settle invoice payment (simulated payment gateway)")
    public ResponseEntity<ApiResponse<InvoiceResponse>> processPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentProcessRequest request) {
        InvoiceResponse response = invoiceService.processPayment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment completed successfully", response));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MECHANIC')")
    @Operation(summary = "Download official tax invoice PDF")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.generatePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
