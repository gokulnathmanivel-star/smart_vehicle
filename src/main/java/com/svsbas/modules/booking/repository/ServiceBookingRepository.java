package com.svsbas.modules.booking.repository;

import com.svsbas.modules.booking.entity.BookingStatus;
import com.svsbas.modules.booking.entity.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    List<ServiceBooking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<ServiceBooking> findByMechanicIdOrderByCreatedAtDesc(Long mechanicId);
    List<ServiceBooking> findByStatus(BookingStatus status);
    Optional<ServiceBooking> findByBookingRef(String bookingRef);
}
