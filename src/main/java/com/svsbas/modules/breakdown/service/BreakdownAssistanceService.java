package com.svsbas.modules.breakdown.service;

import com.svsbas.common.exception.BadRequestException;
import com.svsbas.common.exception.ResourceNotFoundException;
import com.svsbas.common.util.GeoLocationUtil;
import com.svsbas.common.util.ReferenceGenerator;
import com.svsbas.modules.breakdown.dto.BreakdownResponse;
import com.svsbas.modules.breakdown.dto.BreakdownStatusUpdateRequest;
import com.svsbas.modules.breakdown.dto.SosRequest;
import com.svsbas.modules.breakdown.entity.BreakdownRequest;
import com.svsbas.modules.breakdown.entity.BreakdownStatus;
import com.svsbas.modules.breakdown.repository.BreakdownRequestRepository;
import com.svsbas.modules.mechanic.entity.MechanicProfile;
import com.svsbas.modules.mechanic.entity.MechanicStatus;
import com.svsbas.modules.mechanic.repository.MechanicProfileRepository;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.repository.UserRepository;
import com.svsbas.modules.vehicle.entity.Vehicle;
import com.svsbas.modules.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BreakdownAssistanceService {

    private final BreakdownRequestRepository breakdownRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final MechanicProfileRepository mechanicProfileRepository;

    public BreakdownAssistanceService(BreakdownRequestRepository breakdownRepository,
                                      VehicleRepository vehicleRepository,
                                      UserRepository userRepository,
                                      MechanicProfileRepository mechanicProfileRepository) {
        this.breakdownRepository = breakdownRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.mechanicProfileRepository = mechanicProfileRepository;
    }

    @Transactional
    public BreakdownResponse requestSos(String customerEmail, SosRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", customerEmail));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        BreakdownRequest br = new BreakdownRequest();
        br.setSosRef(ReferenceGenerator.generateSosRef());
        br.setCustomer(customer);
        br.setVehicle(vehicle);
        br.setBreakdownType(request.getBreakdownType());
        br.setCustomerLatitude(request.getLatitude());
        br.setCustomerLongitude(request.getLongitude());
        br.setLocationAddress(request.getLocationAddress());
        br.setStatus(BreakdownStatus.DISPATCHED);

        // Geospatial auto-dispatch: find nearest available idle mechanic within 30 km
        List<MechanicProfile> idleMechanics = mechanicProfileRepository.findByIsAvailableTrueAndCurrentStatus(MechanicStatus.IDLE);

        MechanicProfile nearest = idleMechanics.stream()
                .filter(m -> m.getLatitude() != null && m.getLongitude() != null)
                .min(Comparator.comparingDouble(m ->
                        GeoLocationUtil.calculateDistanceKm(request.getLatitude(), request.getLongitude(), m.getLatitude(), m.getLongitude())))
                .orElse(null);

        if (nearest != null) {
            double distance = GeoLocationUtil.calculateDistanceKm(request.getLatitude(), request.getLongitude(), nearest.getLatitude(), nearest.getLongitude());
            if (distance <= 30.0) {
                br.setMechanic(nearest.getUser());
                br.setStatus(BreakdownStatus.MECHANIC_EN_ROUTE);
                br.setAssignedAt(LocalDateTime.now());

                nearest.setCurrentStatus(MechanicStatus.EN_ROUTE);
                nearest.setAvailable(false);
                mechanicProfileRepository.save(nearest);
            }
        }

        BreakdownRequest saved = breakdownRepository.save(br);
        return populateDistanceAndEta(saved);
    }

    public List<BreakdownResponse> getBreakdownsForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (user.getRole() == Role.ROLE_ADMIN) {
            return breakdownRepository.findAll().stream()
                    .map(this::populateDistanceAndEta)
                    .collect(Collectors.toList());
        } else if (user.getRole() == Role.ROLE_MECHANIC) {
            return breakdownRepository.findByMechanicIdOrderByCreatedAtDesc(user.getId()).stream()
                    .map(this::populateDistanceAndEta)
                    .collect(Collectors.toList());
        } else {
            return breakdownRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId()).stream()
                    .map(this::populateDistanceAndEta)
                    .collect(Collectors.toList());
        }
    }

    public BreakdownResponse getBreakdownById(Long id) {
        BreakdownRequest br = breakdownRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breakdown request", "id", id));
        return populateDistanceAndEta(br);
    }

    @Transactional
    public BreakdownResponse dispatchMechanic(Long breakdownId, Long mechanicUserId) {
        BreakdownRequest br = breakdownRepository.findById(breakdownId)
                .orElseThrow(() -> new ResourceNotFoundException("Breakdown request", "id", breakdownId));

        User mechanic = userRepository.findById(mechanicUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic", "id", mechanicUserId));

        br.setMechanic(mechanic);
        br.setStatus(BreakdownStatus.MECHANIC_EN_ROUTE);
        br.setAssignedAt(LocalDateTime.now());

        mechanicProfileRepository.findByUserId(mechanicUserId).ifPresent(p -> {
            p.setCurrentStatus(MechanicStatus.EN_ROUTE);
            p.setAvailable(false);
            mechanicProfileRepository.save(p);
        });

        return populateDistanceAndEta(breakdownRepository.save(br));
    }

    @Transactional
    public BreakdownResponse updateStatus(Long breakdownId, BreakdownStatusUpdateRequest request) {
        BreakdownRequest br = breakdownRepository.findById(breakdownId)
                .orElseThrow(() -> new ResourceNotFoundException("Breakdown request", "id", breakdownId));

        br.setStatus(request.getStatus());
        if (request.getAssessment() != null) br.setInitialAssessment(request.getAssessment());
        if (request.getResolutionNotes() != null) br.setResolutionNotes(request.getResolutionNotes());

        if (request.getStatus() == BreakdownStatus.ON_SITE && br.getArrivedAt() == null) {
            br.setArrivedAt(LocalDateTime.now());
        } else if ((request.getStatus() == BreakdownStatus.RESOLVED || request.getStatus() == BreakdownStatus.TOW_REQUIRED) && br.getResolvedAt() == null) {
            br.setResolvedAt(LocalDateTime.now());
            if (br.getMechanic() != null) {
                mechanicProfileRepository.findByUserId(br.getMechanic().getId()).ifPresent(p -> {
                    p.setCurrentStatus(MechanicStatus.IDLE);
                    p.setAvailable(true);
                    mechanicProfileRepository.save(p);
                });
            }
        }

        return populateDistanceAndEta(breakdownRepository.save(br));
    }

    private BreakdownResponse populateDistanceAndEta(BreakdownRequest br) {
        BreakdownResponse res = new BreakdownResponse(br);
        if (br.getMechanic() != null) {
            mechanicProfileRepository.findByUserId(br.getMechanic().getId()).ifPresent(p -> {
                if (p.getLatitude() != null && p.getLongitude() != null && br.getCustomerLatitude() != null && br.getCustomerLongitude() != null) {
                    double dist = GeoLocationUtil.calculateDistanceKm(
                            br.getCustomerLatitude(), br.getCustomerLongitude(),
                            p.getLatitude(), p.getLongitude());
                    res.setDistanceKm(Math.round(dist * 10.0) / 10.0);
                    res.setEtaMinutes(GeoLocationUtil.estimateEtaMinutes(dist, 30.0));
                }
            });
        }
        return res;
    }
}
