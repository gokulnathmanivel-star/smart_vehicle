package com.svsbas.modules.reporting.service;

import com.svsbas.modules.billing.entity.Invoice;
import com.svsbas.modules.billing.entity.PaymentStatus;
import com.svsbas.modules.billing.repository.InvoiceRepository;
import com.svsbas.modules.booking.entity.BookingStatus;
import com.svsbas.modules.booking.repository.ServiceBookingRepository;
import com.svsbas.modules.breakdown.entity.BreakdownStatus;
import com.svsbas.modules.breakdown.repository.BreakdownRequestRepository;
import com.svsbas.modules.mechanic.entity.MechanicStatus;
import com.svsbas.modules.mechanic.repository.MechanicProfileRepository;
import com.svsbas.modules.reporting.dto.DashboardMetricsResponse;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.repository.UserRepository;
import com.svsbas.modules.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceBookingRepository bookingRepository;
    private final BreakdownRequestRepository breakdownRepository;
    private final MechanicProfileRepository mechanicProfileRepository;
    private final InvoiceRepository invoiceRepository;

    public AnalyticsService(UserRepository userRepository,
                            VehicleRepository vehicleRepository,
                            ServiceBookingRepository bookingRepository,
                            BreakdownRequestRepository breakdownRepository,
                            MechanicProfileRepository mechanicProfileRepository,
                            InvoiceRepository invoiceRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.breakdownRepository = breakdownRepository;
        this.mechanicProfileRepository = mechanicProfileRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardMetricsResponse getDashboardMetrics() {
        long customers = userRepository.findByRole(Role.ROLE_CUSTOMER).size();
        long vehicles = vehicleRepository.count();
        long activeBookings = bookingRepository.findByStatus(BookingStatus.REQUESTED).size()
                + bookingRepository.findByStatus(BookingStatus.ASSIGNED).size()
                + bookingRepository.findByStatus(BookingStatus.IN_PROGRESS).size();

        long activeSos = breakdownRepository.findByStatusIn(List.of(
                BreakdownStatus.DISPATCHED,
                BreakdownStatus.MECHANIC_EN_ROUTE,
                BreakdownStatus.ON_SITE
        )).size();

        long totalMechanics = userRepository.findByRole(Role.ROLE_MECHANIC).size();
        long availableMechanics = mechanicProfileRepository.findByIsAvailableTrueAndCurrentStatus(MechanicStatus.IDLE).size();

        BigDecimal revenue = invoiceRepository.findAll().stream()
                .filter(i -> i.getPaymentStatus() == PaymentStatus.PAID)
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardMetricsResponse(customers, vehicles, activeBookings, activeSos, totalMechanics, availableMechanics, revenue);
    }
}
