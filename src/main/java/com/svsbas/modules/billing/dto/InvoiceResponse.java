package com.svsbas.modules.billing.dto;

import com.svsbas.modules.billing.entity.Invoice;
import com.svsbas.modules.billing.entity.ItemType;
import com.svsbas.modules.billing.entity.PaymentMode;
import com.svsbas.modules.billing.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceResponse {
    private Long id;
    private String invoiceRef;
    private String bookingRef;
    private String sosRef;
    private String customerName;
    private String vehicleInfo;
    private BigDecimal totalLaborCost;
    private BigDecimal totalPartsCost;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private PaymentMode paymentMode;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private List<InvoiceItemDto> items;

    public static class InvoiceItemDto {
        private Long id;
        private ItemType itemType;
        private String description;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public InvoiceItemDto() {}
        public InvoiceItemDto(Long id, ItemType itemType, String description, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {
            this.id = id;
            this.itemType = itemType;
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public ItemType getItemType() { return itemType; }
        public void setItemType(ItemType itemType) { this.itemType = itemType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    public InvoiceResponse() {}

    public InvoiceResponse(Invoice inv) {
        this.id = inv.getId();
        this.invoiceRef = inv.getInvoiceRef();
        if (inv.getBooking() != null) {
            this.bookingRef = inv.getBooking().getBookingRef();
            if (inv.getBooking().getCustomer() != null) {
                this.customerName = inv.getBooking().getCustomer().getFullName();
            }
            if (inv.getBooking().getVehicle() != null) {
                this.vehicleInfo = inv.getBooking().getVehicle().getBrand() + " " + inv.getBooking().getVehicle().getModel();
            }
        }
        if (inv.getBreakdown() != null) {
            this.sosRef = inv.getBreakdown().getSosRef();
            if (inv.getBreakdown().getCustomer() != null) {
                this.customerName = inv.getBreakdown().getCustomer().getFullName();
            }
            if (inv.getBreakdown().getVehicle() != null) {
                this.vehicleInfo = inv.getBreakdown().getVehicle().getBrand() + " " + inv.getBreakdown().getVehicle().getModel();
            }
        }
        this.totalLaborCost = inv.getTotalLaborCost();
        this.totalPartsCost = inv.getTotalPartsCost();
        this.taxRate = inv.getTaxRate();
        this.taxAmount = inv.getTaxAmount();
        this.totalAmount = inv.getTotalAmount();
        this.paymentStatus = inv.getPaymentStatus();
        this.paymentMode = inv.getPaymentMode();
        this.paidAt = inv.getPaidAt();
        this.createdAt = inv.getCreatedAt();
        if (inv.getItems() != null) {
            this.items = inv.getItems().stream()
                    .map(it -> new InvoiceItemDto(it.getId(), it.getItemType(), it.getDescription(), it.getQuantity(), it.getUnitPrice(), it.getSubtotal()))
                    .collect(Collectors.toList());
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceRef() { return invoiceRef; }
    public void setInvoiceRef(String invoiceRef) { this.invoiceRef = invoiceRef; }
    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
    public String getSosRef() { return sosRef; }
    public void setSosRef(String sosRef) { this.sosRef = sosRef; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }
    public BigDecimal getTotalLaborCost() { return totalLaborCost; }
    public void setTotalLaborCost(BigDecimal totalLaborCost) { this.totalLaborCost = totalLaborCost; }
    public BigDecimal getTotalPartsCost() { return totalPartsCost; }
    public void setTotalPartsCost(BigDecimal totalPartsCost) { this.totalPartsCost = totalPartsCost; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(PaymentMode paymentMode) { this.paymentMode = paymentMode; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<InvoiceItemDto> getItems() { return items; }
    public void setItems(List<InvoiceItemDto> items) { this.items = items; }
}
