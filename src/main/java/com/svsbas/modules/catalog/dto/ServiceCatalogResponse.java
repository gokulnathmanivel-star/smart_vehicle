package com.svsbas.modules.catalog.dto;

import com.svsbas.modules.catalog.entity.ServiceCatalogItem;
import com.svsbas.modules.catalog.entity.ServiceCategory;

import java.math.BigDecimal;

public class ServiceCatalogResponse {
    private Long id;
    private String serviceCode;
    private String serviceName;
    private ServiceCategory category;
    private BigDecimal basePrice;
    private BigDecimal estimatedHours;
    private String description;
    private boolean active;

    public ServiceCatalogResponse() {}

    public ServiceCatalogResponse(ServiceCatalogItem item) {
        this.id = item.getId();
        this.serviceCode = item.getServiceCode();
        this.serviceName = item.getServiceName();
        this.category = item.getCategory();
        this.basePrice = item.getBasePrice();
        this.estimatedHours = item.getEstimatedHours();
        this.description = item.getDescription();
        this.active = item.isActive();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public ServiceCategory getCategory() { return category; }
    public void setCategory(ServiceCategory category) { this.category = category; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public BigDecimal getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
