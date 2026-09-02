package com.svsbas.modules.booking.service;

import com.svsbas.common.exception.BadRequestException;
import com.svsbas.common.exception.ResourceNotFoundException;
import com.svsbas.common.util.ReferenceGenerator;
import com.svsbas.modules.booking.dto.AssignMechanicRequest;
import com.svsbas.modules.booking.dto.BookingCreateRequest;
import com.svsbas.modules.booking.dto.BookingResponse;
import com.svsbas.modules.booking.dto.BookingStatusUpdateRequest;
import com.svsbas.modules.booking.entity.BookingServiceJunction;
import com.svsbas.modules.booking.entity.BookingStatus;
import com.svsbas.modules.booking.entity.ServiceBooking;
import com.svsbas.modules.booking.repository.ServiceBookingRepository;
import com.svsbas.modules.catalog.entity.ServiceCatalogItem;
import com.svsbas.modules.catalog.repository.ServiceCatalogRepository;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.repository.UserRepository;
import com.svsbas.modules.vehicle.entity.Vehicle;
import com.svsbas.modules.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceBookingService {

    private final ServiceBookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceCatalogRepository catalogRepository;

    public ServiceBookingService(ServiceBookingRepository bookingRepository,
                                 VehicleRepository vehicleRepository,
                                 UserRepository userRepository,
                                 ServiceCatalogRepository catalogRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.catalogRepository = catalogRepository;
    }

    @Transactional
    public BookingResponse createBooking(String customerEmail, BookingCreateRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", customerEmail));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        if (!vehicle.getUser().getId().equals(customer.getId()) && customer.getRole() != Role.ROLE_ADMIN) {
            throw new BadRequestException("You can only book services for your own registered vehicles");
        }

        ServiceBooking booking = new ServiceBooking();
        booking.setBookingRef(ReferenceGenerator.generateBookingRef());
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setPreferredSlot(request.getPreferredSlot());
        booking.setCustomerNotes(request.getCustomerNotes());
        booking.setStatus(BookingStatus.REQUESTED);

        BigDecimal total = BigDecimal.ZERO;
        for (Long catId : request.getServiceCatalogIds()) {
            ServiceCatalogItem item = catalogRepository.findById(catId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service item", "id", catId));
            BookingServiceJunction junction = new BookingServiceJunction(booking, item, item.getBasePrice());
            booking.getServices().add(junction);
            total = total.add(item.getBasePrice());
        }

        booking.setEstimatedCost(total);
        ServiceBooking saved = bookingRepository.save(booking);
        return new BookingResponse(saved);
    }

    public List<BookingResponse> getBookingsForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (user.getRole() == Role.ROLE_ADMIN) {
            return bookingRepository.findAll().stream().map(BookingResponse::new).collect(Collectors.toList());
        } else if (user.getRole() == Role.ROLE_MECHANIC) {
            return bookingRepository.findByMechanicIdOrderByCreatedAtDesc(user.getId()).stream()
                    .map(BookingResponse::new).collect(Collectors.toList());
        } else {
            return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()).stream()
                    .map(BookingResponse::new).collect(Collectors.toList());
        }
    }

    public BookingResponse getBookingById(Long id) {
        ServiceBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Booking", "id", id));
        return new BookingResponse(booking);
    }

    @Transactional
    public BookingResponse assignMechanic(Long bookingId, AssignMechanicRequest request) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Booking", "id", bookingId));

        User mechanic = userRepository.findById(request.getMechanicUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic", "id", request.getMechanicUserId()));

        if (mechanic.getRole() != Role.ROLE_MECHANIC) {
            throw new BadRequestException("User selected is not a certified mechanic");
        }

        booking.setMechanic(mechanic);
        booking.setStatus(BookingStatus.ASSIGNED);
        return new BookingResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse updateStatus(Long bookingId, BookingStatusUpdateRequest request) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Booking", "id", bookingId));

        booking.setStatus(request.getStatus());
        if (request.getMechanicNotes() != null) {
            booking.setMechanicNotes(request.getMechanicNotes());
        }

        if (request.getStatus() == BookingStatus.IN_PROGRESS && booking.getActualStartTime() == null) {
            booking.setActualStartTime(LocalDateTime.now());
        } else if ((request.getStatus() == BookingStatus.INVOICED || request.getStatus() == BookingStatus.COMPLETED) && booking.getActualEndTime() == null) {
            booking.setActualEndTime(LocalDateTime.now());
        }

        return new BookingResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Booking", "id", bookingId));

        if (booking.getStatus() == BookingStatus.IN_PROGRESS || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a booking that is currently in progress or completed");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return new BookingResponse(bookingRepository.save(booking));
    }
}
