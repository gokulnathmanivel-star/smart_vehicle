package com.svsbas.modules.mechanic.service;

import com.svsbas.common.exception.ResourceNotFoundException;
import com.svsbas.modules.mechanic.dto.MechanicProfileResponse;
import com.svsbas.modules.mechanic.entity.MechanicProfile;
import com.svsbas.modules.mechanic.entity.MechanicStatus;
import com.svsbas.modules.mechanic.repository.MechanicProfileRepository;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MechanicService {

    private final MechanicProfileRepository profileRepository;
    private final UserRepository userRepository;

    public MechanicService(MechanicProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
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
    public MechanicProfileResponse updateStatus(String email, MechanicStatus status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        MechanicProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile", "userId", user.getId()));

        profile.setCurrentStatus(status);
        profile.setAvailable(status == MechanicStatus.IDLE);
        return new MechanicProfileResponse(profileRepository.save(profile));
    }

    @Transactional
    public MechanicProfileResponse updateLocation(String email, Double lat, Double lon) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        MechanicProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic profile", "userId", user.getId()));

        profile.setLatitude(lat);
        profile.setLongitude(lon);
        return new MechanicProfileResponse(profileRepository.save(profile));
    }
}
