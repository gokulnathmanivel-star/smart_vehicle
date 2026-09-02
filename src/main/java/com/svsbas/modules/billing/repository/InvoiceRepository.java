package com.svsbas.modules.billing.repository;

import com.svsbas.modules.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByBookingId(Long bookingId);
    Optional<Invoice> findByBreakdownId(Long breakdownId);
    Optional<Invoice> findByInvoiceRef(String invoiceRef);
}
