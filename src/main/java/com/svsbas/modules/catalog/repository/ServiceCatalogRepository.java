package com.svsbas.modules.catalog.repository;

import com.svsbas.modules.catalog.entity.ServiceCatalogItem;
import com.svsbas.modules.catalog.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalogItem, Long> {
    List<ServiceCatalogItem> findByIsActiveTrue();
    List<ServiceCatalogItem> findByCategoryAndIsActiveTrue(ServiceCategory category);
    Optional<ServiceCatalogItem> findByServiceCode(String serviceCode);
    boolean existsByServiceCode(String serviceCode);
}
