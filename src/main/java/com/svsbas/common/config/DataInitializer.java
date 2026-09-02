package com.svsbas.common.config;

import com.svsbas.modules.catalog.entity.ServiceCatalogItem;
import com.svsbas.modules.catalog.entity.ServiceCategory;
import com.svsbas.modules.catalog.repository.ServiceCatalogRepository;
import com.svsbas.modules.mechanic.entity.MechanicProfile;
import com.svsbas.modules.mechanic.repository.MechanicProfileRepository;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.repository.UserRepository;
import com.svsbas.modules.vehicle.entity.FuelType;
import com.svsbas.modules.vehicle.entity.Vehicle;
import com.svsbas.modules.vehicle.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            VehicleRepository vehicleRepository,
            ServiceCatalogRepository catalogRepository,
            MechanicProfileRepository mechanicProfileRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Seed Users
            if (!userRepository.existsByEmail("admin@demo.com")) {
                User admin = new User("admin@demo.com", "+91 99887 76655", passwordEncoder.encode("password123"), "Anita Desai (Admin)", Role.ROLE_ADMIN);
                userRepository.save(admin);
                logger.info("Created demo Admin account: admin@demo.com");
            }

            User mechanic = userRepository.findByEmail("mechanic@demo.com").orElse(null);
            if (mechanic == null) {
                mechanic = new User("mechanic@demo.com", "+91 91234 56789", passwordEncoder.encode("password123"), "Vikram Singh (Lead Tech)", Role.ROLE_MECHANIC);
                mechanic = userRepository.save(mechanic);
                MechanicProfile profile = new MechanicProfile(mechanic, "Engine Diagnostics & Roadside SOS", 12.9562, 77.7019);
                mechanicProfileRepository.save(profile);
                logger.info("Created demo Mechanic account: mechanic@demo.com");
            }

            User customer = userRepository.findByEmail("customer@demo.com").orElse(null);
            if (customer == null) {
                customer = new User("customer@demo.com", "+91 98765 43210", passwordEncoder.encode("password123"), "Rahul Sharma", Role.ROLE_CUSTOMER);
                customer = userRepository.save(customer);
                logger.info("Created demo Customer account: customer@demo.com");

                // Seed customer vehicles
                Vehicle v1 = new Vehicle(customer, "KA-01-MJ-5021", "Hyundai", "Creta SX (O)", FuelType.PETROL, 2022, 28500);
                Vehicle v2 = new Vehicle(customer, "KA-05-EV-9912", "Tata", "Nexon EV Max", FuelType.ELECTRIC, 2023, 14200);
                vehicleRepository.save(v1);
                vehicleRepository.save(v2);
                logger.info("Created demo vehicles for customer");
            }

            // 2. Seed Service Catalog Items
            if (catalogRepository.count() == 0) {
                catalogRepository.save(new ServiceCatalogItem("SVC-GEN-01", "Comprehensive Periodic Maintenance", ServiceCategory.GENERAL_MAINTENANCE, BigDecimal.valueOf(2999.00), BigDecimal.valueOf(3.5), "Complete 40-point vehicle inspection, synthetic oil & filter change."));
                catalogRepository.save(new ServiceCatalogItem("SVC-ENG-02", "Synthetic Engine Oil & Filter Flush", ServiceCategory.ENGINE_REPAIR, BigDecimal.valueOf(1850.00), BigDecimal.valueOf(1.0), "High-grade 5W-30 synthetic oil with OEM filter."));
                catalogRepository.save(new ServiceCatalogItem("SVC-BRK-03", "Brake Pad Inspection & Fluid Replacement", ServiceCategory.TYRES_BRAKES, BigDecimal.valueOf(1200.00), BigDecimal.valueOf(1.5), "Front & rear caliper cleaning, pad wear check, DOT4 flush."));
                catalogRepository.save(new ServiceCatalogItem("SVC-WHL-04", "3D Wheel Alignment & Laser Balancing", ServiceCategory.TYRES_BRAKES, BigDecimal.valueOf(950.00), BigDecimal.valueOf(1.0), "Laser computerized 4-wheel alignment and counterweights."));
                catalogRepository.save(new ServiceCatalogItem("SVC-AC-05", "Air Conditioning & Cabin Disinfection", ServiceCategory.ELECTRICAL, BigDecimal.valueOf(1450.00), BigDecimal.valueOf(1.5), "AC gas refill, condenser coil cleaning, anti-bacterial fogging."));
                catalogRepository.save(new ServiceCatalogItem("SVC-SOS-06", "Emergency Roadside Dispatch Surcharge", ServiceCategory.EMERGENCY_ROADSIDE, BigDecimal.valueOf(799.00), BigDecimal.valueOf(1.0), "Immediate field unit deployment and on-site troubleshooting."));
                logger.info("Initialized Service Catalog with 6 standard service tariffs");
            }
        };
    }
}
