package com.svsbas.modules.breakdown.repository;

import com.svsbas.modules.breakdown.entity.BreakdownRequest;
import com.svsbas.modules.breakdown.entity.BreakdownStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreakdownRequestRepository extends JpaRepository<BreakdownRequest, Long> {
    List<BreakdownRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<BreakdownRequest> findByMechanicIdOrderByCreatedAtDesc(Long mechanicId);
    List<BreakdownRequest> findByStatus(BreakdownStatus status);
    List<BreakdownRequest> findByStatusIn(List<BreakdownStatus> statuses);
    Optional<BreakdownRequest> findBySosRef(String sosRef);
}
