package com.svsbas.modules.catalog.dto;

import com.svsbas.modules.catalog.entity.ServiceCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ServiceCatalogRequest {

    @NotBlank(message = "Service code is required")
    private String serviceCode;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotNull(message = "Category is required")
    private ServiceCategory category;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal basePrice;

    @NotNull(message = "Estimated hours is required")
    @DecimalMin(value = "0.1", message = "Hours must be at least 0.1")
    private BigDecimal estimatedHours;

    private String description;

    public ServiceCatalogRequest() {}

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
}
