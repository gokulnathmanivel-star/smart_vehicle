package com.svsbas.modules.notification.scheduler;

import com.svsbas.modules.vehicle.entity.Vehicle;
import com.svsbas.modules.vehicle.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceReminderScheduler.class);
    private final VehicleRepository vehicleRepository;

    public ServiceReminderScheduler(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Runs every day at 08:00 AM (and every 60 seconds during initial demo) to evaluate vehicles
     * approaching 10,000 km periodic maintenance intervals.
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 15000)
    public void evaluateServiceReminders() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        for (Vehicle v : vehicles) {
            if (v.getCurrentMileage() != null && v.getCurrentMileage() >= 10000) {
                logger.info("[MAINTENANCE REMINDER SCHEDULER] Vehicle: {} [{}] with {} km is flagged for scheduled periodic maintenance.",
                        v.getBrand() + " " + v.getModel(), v.getRegNumber(), v.getCurrentMileage());
            }
        }
    }
}
