package com.svsbas.modules.mechanic.repository;

import com.svsbas.modules.mechanic.entity.MechanicProfile;
import com.svsbas.modules.mechanic.entity.MechanicStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MechanicProfileRepository extends JpaRepository<MechanicProfile, Long> {
    Optional<MechanicProfile> findByUserId(Long userId);
    List<MechanicProfile> findByIsAvailableTrueAndCurrentStatus(MechanicStatus status);
}
