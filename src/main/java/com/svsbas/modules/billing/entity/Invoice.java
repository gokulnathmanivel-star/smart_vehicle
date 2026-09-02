package com.svsbas.modules.billing.entity;

import com.svsbas.modules.booking.entity.ServiceBooking;
import com.svsbas.modules.breakdown.entity.BreakdownRequest;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_ref", nullable = false, unique = true, length = 36)
    private String invoiceRef;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true)
    private ServiceBooking booking;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breakdown_id", unique = true)
    private BreakdownRequest breakdown;

    @Column(name = "total_labor_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLaborCost = BigDecimal.ZERO;

    @Column(name = "total_parts_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPartsCost = BigDecimal.ZERO;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate = BigDecimal.valueOf(18.00);

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", length = 20)
    private PaymentMode paymentMode;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Invoice() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceRef() { return invoiceRef; }
    public void setInvoiceRef(String invoiceRef) { this.invoiceRef = invoiceRef; }
    public ServiceBooking getBooking() { return booking; }
    public void setBooking(ServiceBooking booking) { this.booking = booking; }
    public BreakdownRequest getBreakdown() { return breakdown; }
    public void setBreakdown(BreakdownRequest breakdown) { this.breakdown = breakdown; }
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
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
