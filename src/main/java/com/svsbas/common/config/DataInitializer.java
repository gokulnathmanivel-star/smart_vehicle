package com.svsbas.common.config;

import com.svsbas.modules.booking.entity.BookingServiceJunction;
import com.svsbas.modules.booking.entity.BookingStatus;
import com.svsbas.modules.booking.entity.ServiceBooking;
import com.svsbas.modules.booking.repository.ServiceBookingRepository;
import com.svsbas.modules.breakdown.entity.BreakdownRequest;
import com.svsbas.modules.breakdown.entity.BreakdownStatus;
import com.svsbas.modules.breakdown.entity.BreakdownType;
import com.svsbas.modules.breakdown.repository.BreakdownRequestRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            VehicleRepository vehicleRepository,
            ServiceCatalogRepository catalogRepository,
            MechanicProfileRepository mechanicProfileRepository,
            ServiceBookingRepository bookingRepository,
            BreakdownRequestRepository breakdownRepository,
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

            User deepak = userRepository.findByEmail("deepak@gmail.com").orElse(null);
            if (deepak == null) {
                deepak = new User("deepak@gmail.com", "8464392875", passwordEncoder.encode("password123"), "Deepak", Role.ROLE_MECHANIC);
                deepak = userRepository.save(deepak);
                MechanicProfile deepakProfile = new MechanicProfile(deepak, "Engine Developer", 12.9716, 77.5946);
                mechanicProfileRepository.save(deepakProfile);
                logger.info("Created demo Mechanic account: deepak@gmail.com");
            }

            User customer = userRepository.findByEmail("customer@demo.com").orElse(null);
            Vehicle v1 = null;
            Vehicle v2 = null;
            if (customer == null) {
                customer = new User("customer@demo.com", "+91 98765 43210", passwordEncoder.encode("password123"), "Rahul Sharma", Role.ROLE_CUSTOMER);
                customer = userRepository.save(customer);
                logger.info("Created demo Customer account: customer@demo.com");

                // Seed customer vehicles
                v1 = new Vehicle(customer, "KA-01-MJ-5021", "Hyundai", "Creta SX (O)", FuelType.PETROL, 2022, 28500);
                v2 = new Vehicle(customer, "KA-05-EV-9912", "Tata", "Nexon EV Max", FuelType.ELECTRIC, 2023, 14200);
                v1 = vehicleRepository.save(v1);
                v2 = vehicleRepository.save(v2);
                logger.info("Created demo vehicles for customer");
            } else {
                v1 = vehicleRepository.findByRegNumber("KA-01-MJ-5021").orElse(null);
                v2 = vehicleRepository.findByRegNumber("KA-05-EV-9912").orElse(null);
            }

            // 2. Seed Service Catalog Items
            ServiceCatalogItem item1 = null;
            ServiceCatalogItem item3 = null;
            if (catalogRepository.count() == 0) {
                item1 = catalogRepository.save(new ServiceCatalogItem("SVC-GEN-01", "Comprehensive Periodic Maintenance", ServiceCategory.GENERAL_MAINTENANCE, BigDecimal.valueOf(2999.00), BigDecimal.valueOf(3.5), "Complete 40-point vehicle inspection, synthetic oil & filter change."));
                catalogRepository.save(new ServiceCatalogItem("SVC-ENG-02", "Synthetic Engine Oil & Filter Flush", ServiceCategory.ENGINE_REPAIR, BigDecimal.valueOf(1850.00), BigDecimal.valueOf(1.0), "High-grade 5W-30 synthetic oil with OEM filter."));
                item3 = catalogRepository.save(new ServiceCatalogItem("SVC-BRK-03", "Brake Pad Inspection & Fluid Replacement", ServiceCategory.TYRES_BRAKES, BigDecimal.valueOf(1200.00), BigDecimal.valueOf(1.5), "Front & rear caliper cleaning, pad wear check, DOT4 flush."));
                catalogRepository.save(new ServiceCatalogItem("SVC-WHL-04", "3D Wheel Alignment & Laser Balancing", ServiceCategory.TYRES_BRAKES, BigDecimal.valueOf(950.00), BigDecimal.valueOf(1.0), "Laser computerized 4-wheel alignment and counterweights."));
                catalogRepository.save(new ServiceCatalogItem("SVC-AC-05", "Air Conditioning & Cabin Disinfection", ServiceCategory.ELECTRICAL, BigDecimal.valueOf(1450.00), BigDecimal.valueOf(1.5), "AC gas refill, condenser coil cleaning, anti-bacterial fogging."));
                catalogRepository.save(new ServiceCatalogItem("SVC-SOS-06", "Emergency Roadside Dispatch Surcharge", ServiceCategory.EMERGENCY_ROADSIDE, BigDecimal.valueOf(799.00), BigDecimal.valueOf(1.0), "Immediate field unit deployment and on-site troubleshooting."));
                logger.info("Initialized Service Catalog with 6 standard service tariffs");
            } else {
                item1 = catalogRepository.findByServiceCode("SVC-GEN-01").orElse(null);
                item3 = catalogRepository.findByServiceCode("SVC-BRK-03").orElse(null);
            }

            // 3. Seed Initial Workshop Service Bookings assigned to Vikram Singh
            if (bookingRepository.count() == 0 && customer != null && v1 != null && item1 != null) {
                ServiceBooking b1 = new ServiceBooking(v1, customer, LocalDateTime.now().plusDays(2).withHour(10).withMinute(30).withSecond(0));
                b1.setBookingRef("SB-2026-0819");
                b1.setCustomerNotes("Scheduled 30,000 km periodic service + slight brake squeal");
                b1.setMechanic(mechanic);
                b1.setStatus(BookingStatus.IN_PROGRESS);
                b1.setEstimatedCost(item1.getBasePrice());
                BookingServiceJunction j1 = new BookingServiceJunction(b1, item1, item1.getBasePrice());
                b1.getServices().add(j1);
                bookingRepository.save(b1);
                logger.info("Seeded active workshop booking SB-2026-0819 assigned to Vikram Singh");
            }

            // 4. Seed Initial Emergency Roadside SOS Dispatch assigned to Vikram Singh
            if (breakdownRepository.count() == 0 && customer != null && v2 != null) {
                BreakdownRequest br1 = new BreakdownRequest();
                br1.setSosRef("SOS-2026-0902-881");
                br1.setCustomer(customer);
                br1.setVehicle(v2);
                br1.setBreakdownType(BreakdownType.FLAT_TYRE);
                br1.setCustomerLatitude(12.9562);
                br1.setCustomerLongitude(77.7019);
                br1.setLocationAddress("Outer Ring Road, Near Marathahalli Bridge, Bengaluru");
                br1.setStatus(BreakdownStatus.MECHANIC_EN_ROUTE);
                br1.setMechanic(mechanic);
                br1.setAssignedAt(LocalDateTime.now().minusMinutes(14));
                breakdownRepository.save(br1);
                logger.info("Seeded active emergency SOS SOS-2026-0902-881 assigned to Vikram Singh");
            }
        };
    }
}
