package com.svsbas.modules.vehicle.service;

import com.svsbas.common.exception.BadRequestException;
import com.svsbas.common.exception.ResourceNotFoundException;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.repository.UserRepository;
import com.svsbas.modules.vehicle.dto.VehicleCreateRequest;
import com.svsbas.modules.vehicle.dto.VehicleResponse;
import com.svsbas.modules.vehicle.entity.Vehicle;
import com.svsbas.modules.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public VehicleResponse createVehicle(String userEmail, VehicleCreateRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        String normalizedReg = request.getRegNumber().trim().toUpperCase();

        if (vehicleRepository.existsByRegNumber(normalizedReg)) {
            throw new BadRequestException("A vehicle is already registered with registration number: " + normalizedReg);
        }

        Vehicle vehicle = new Vehicle(
                user,
                normalizedReg,
                request.getBrand().trim(),
                request.getModel().trim(),
                request.getFuelType(),
                request.getManufactureYear(),
                request.getCurrentMileage()
        );

        Vehicle saved = vehicleRepository.save(vehicle);
        return new VehicleResponse(saved);
    }

    public List<VehicleResponse> getVehiclesForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (user.getRole() == Role.ROLE_ADMIN) {
            return vehicleRepository.findAll().stream().map(VehicleResponse::new).collect(Collectors.toList());
        }

        return vehicleRepository.findByUserId(user.getId()).stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }

    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return new VehicleResponse(vehicle);
    }

    @Transactional
    public void deleteVehicle(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        if (user.getRole() != Role.ROLE_ADMIN && !vehicle.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have authorization to remove this vehicle");
        }

        vehicleRepository.delete(vehicle);
    }
}
