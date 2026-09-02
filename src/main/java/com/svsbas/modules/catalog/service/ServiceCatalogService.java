package com.svsbas.modules.catalog.service;

import com.svsbas.common.exception.BadRequestException;
import com.svsbas.common.exception.ResourceNotFoundException;
import com.svsbas.modules.catalog.dto.ServiceCatalogRequest;
import com.svsbas.modules.catalog.dto.ServiceCatalogResponse;
import com.svsbas.modules.catalog.entity.ServiceCatalogItem;
import com.svsbas.modules.catalog.entity.ServiceCategory;
import com.svsbas.modules.catalog.repository.ServiceCatalogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCatalogService {

    private final ServiceCatalogRepository catalogRepository;

    public ServiceCatalogService(ServiceCatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public List<ServiceCatalogResponse> getActiveServices(ServiceCategory category) {
        List<ServiceCatalogItem> items = (category != null)
                ? catalogRepository.findByCategoryAndIsActiveTrue(category)
                : catalogRepository.findByIsActiveTrue();

        return items.stream().map(ServiceCatalogResponse::new).collect(Collectors.toList());
    }

    public List<ServiceCatalogResponse> getAllServices() {
        return catalogRepository.findAll().stream().map(ServiceCatalogResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public ServiceCatalogResponse createServiceItem(ServiceCatalogRequest request) {
        if (catalogRepository.existsByServiceCode(request.getServiceCode())) {
            throw new BadRequestException("Service code already exists: " + request.getServiceCode());
        }

        ServiceCatalogItem item = new ServiceCatalogItem(
                request.getServiceCode().toUpperCase(),
                request.getServiceName(),
                request.getCategory(),
                request.getBasePrice(),
                request.getEstimatedHours(),
                request.getDescription()
        );

        return new ServiceCatalogResponse(catalogRepository.save(item));
    }

    @Transactional
    public ServiceCatalogResponse updateServiceItem(Long id, ServiceCatalogRequest request) {
        ServiceCatalogItem item = catalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service item", "id", id));

        item.setServiceName(request.getServiceName());
        item.setCategory(request.getCategory());
        item.setBasePrice(request.getBasePrice());
        item.setEstimatedHours(request.getEstimatedHours());
        item.setDescription(request.getDescription());

        return new ServiceCatalogResponse(catalogRepository.save(item));
    }

    @Transactional
    public void toggleActiveStatus(Long id) {
        ServiceCatalogItem item = catalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service item", "id", id));

        item.setActive(!item.isActive());
        catalogRepository.save(item);
    }
}
