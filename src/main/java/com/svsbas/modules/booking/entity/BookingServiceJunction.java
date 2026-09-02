package com.svsbas.modules.booking.entity;

import com.svsbas.modules.catalog.entity.ServiceCatalogItem;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_services")
public class BookingServiceJunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private ServiceBooking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id", nullable = false)
    private ServiceCatalogItem catalogItem;

    @Column(name = "agreed_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal agreedPrice;

    public BookingServiceJunction() {}

    public BookingServiceJunction(ServiceBooking booking, ServiceCatalogItem catalogItem, BigDecimal agreedPrice) {
        this.booking = booking;
        this.catalogItem = catalogItem;
        this.agreedPrice = agreedPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ServiceBooking getBooking() { return booking; }
    public void setBooking(ServiceBooking booking) { this.booking = booking; }
    public ServiceCatalogItem getCatalogItem() { return catalogItem; }
    public void setCatalogItem(ServiceCatalogItem catalogItem) { this.catalogItem = catalogItem; }
    public BigDecimal getAgreedPrice() { return agreedPrice; }
    public void setAgreedPrice(BigDecimal agreedPrice) { this.agreedPrice = agreedPrice; }
}
