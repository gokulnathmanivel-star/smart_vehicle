package com.svsbas.modules.mechanic.service;

import com.svsbas.common.exception.BadRequestException;
import com.svsbas.common.exception.ResourceNotFoundException;
import com.svsbas.modules.mechanic.dto.MechanicCreateRequest;
import com.svsbas.modules.mechanic.dto.MechanicProfileResponse;
import com.svsbas.modules.mechanic.entity.MechanicProfile;
import com.svsbas.modules.mechanic.entity.MechanicStatus;
import com.svsbas.modules.mechanic.repository.MechanicProfileRepository;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MechanicService {

    private final MechanicProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MechanicService(MechanicProfileRepository profileRepository, 
                           UserRepository userRepository, 
                           PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<MechanicProfileResponse> getAllMechanics(boolean availableOnly) {
        if (availableOnly) {
            return profileRepository.findByIsAvailableTrueAndCurrentStatus(MechanicStatus.IDLE).stream()
                    .map(MechanicProfileResponse::new)
                    .collect(Collectors.toList());
        }
        return profileRepository.findAll().stream()
                .map(MechanicProfileResponse::new)
                .collect(Collectors.toList());
    }

    public MechanicProfileResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        MechanicProfile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    MechanicProfile p = new MechanicProfile(user, "General Auto Technician", 12.9716, 77.5946);
                    return profileRepository.save(p);
                });

        return new MechanicProfileResponse(profile);
    }

    @Transactional
    public MechanicProfileResponse registerMechanic(MechanicCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        String rawPassword = (request.getPassword() != null && !request.getPassword().isBlank())
                ? request.getPassword()
                : "password123";

        User user = new User(
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(rawPassword),
                request.getFullName(),
                Role.ROLE_MECHANIC
        );
        user = userRepository.save(user);

        String specialization = (request.getSpecialization() != null && !request.getSpecialization().isBlank())
                ? request.getSpecialization()
                : "General Automotive Diagnostics";

        MechanicProfile profile = new MechanicProfile(user, specialization, 12.9716, 77.5946);
        profile = profileRepository.save(profile);

        return new MechanicProfileResponse(profile);
    }

    @Transactional
    public MechanicProfileResponse updateStatus(String email, MechanicStatus status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        MechanicProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile", "userId", user.getId()));

        profile.setCurrentStatus(status);
        profile.setAvailable(status == MechanicStatus.IDLE);
        profile = profileRepository.save(profile);
        return new MechanicProfileResponse(profile);
    }

    @Transactional
    public MechanicProfileResponse updateLocation(String email, Double latitude, Double longitude) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        MechanicProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile", "userId", user.getId()));

        profile.setLatitude(latitude);
        profile.setLongitude(longitude);
        profile = profileRepository.save(profile);
        return new MechanicProfileResponse(profile);
    }
}
