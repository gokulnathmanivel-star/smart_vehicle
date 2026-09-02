# Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)

An enterprise-grade academic final-year project designed for planned workshop maintenance and rapid 24/7 roadside emergency assistance (SOS) with real-time GPS tracking, nearest-mechanic auto-dispatch (Haversine formula), and dynamic GST invoicing.

---

## 1. Technology Stack

- **Backend**: Java 17 / JDK 24, Spring Boot 3.3.3, Spring Data JPA / Hibernate 6.5, Apache Maven 3.9.9
- **Security**: Spring Security 6 (Stateless JWT architecture, BCrypt password hashing, method-level `@PreAuthorize`, RBAC)
- **Database**: MySQL 8.x (Dual-profile support with zero-config in-memory H2 fallback for instant evaluation)
- **Frontend**: Vanilla HTML5, CSS3 (Modern Flexbox/CSS Variables/Grid), JavaScript ES6+ (Fetch API, DOM manipulation)
- **Libraries**:
  - `io.jsonwebtoken:jjwt:0.12.6` (JSON Web Tokens)
  - `springdoc-openapi-starter-webmvc-ui:2.6.0` (Swagger UI)
  - `com.github.librepdf:openpdf:2.0.3` (PDF Tax Invoice Generator)
  - Bootstrap 5.3 & Font Awesome 6.4 (via CDN)

---

## 2. System Architecture & Features

### Core Modules
1. **Authentication & RBAC**:
   - 3 distinct user roles: `ROLE_CUSTOMER`, `ROLE_MECHANIC`, `ROLE_ADMIN`.
   - Stateless JWT Bearer token authentication with automatic token injection in frontend requests.
2. **Customer Garage**:
   - Multi-vehicle registry with VIN/plate uniqueness checks, fuel type selection, and odometer tracking.
3. **Service Booking Workflow**:
   - Multi-package selection, scheduled slot reservation, status tracking (`REQUESTED` → `ASSIGNED` → `IN_PROGRESS` → `INVOICED` → `COMPLETED`).
4. **Emergency Roadside Assistance (SOS)**:
   - 1-click SOS button with HTML5 Geolocation.
   - **Haversine Algorithm Auto-Dispatch**: Automatically locates and dispatches the closest available mechanic within a 30 km radius.
   - Computes real-time travel distance (km) and estimated time of arrival (ETA in minutes).
5. **Field Mechanic Portal**:
   - Ticket dashboard, GPS telemetry updates, labor hour logging, and dynamic replacement parts billing.
6. **Billing & PDF Invoicing**:
   - Dynamic itemized bills (Labor + Services + Parts + 18% GST).
   - Simulated UPI/Cash/Card payments and downloadable PDF invoices generated via OpenPDF.
7. **Maintenance Scheduler**:
   - Automated periodic service alert scheduler evaluating vehicles approaching 10,000 km intervals.
8. **Admin Operations & Analytics**:
   - Central command center monitoring active tickets, mechanic availability, fleet inventory, and total platform revenue.

---

## 3. Directory Layout

```text
Project1/
├── pom.xml                                   # Spring Boot Maven build definition
├── svsbas_postman_collection.json            # Automated Postman API test collection
├── svsbas_postman_environment.json           # Postman environment variables
├── README.md                                 # Complete documentation
│
├── frontend/                                 # 20 HTML5 pages + UI assets
│   ├── index.html                            # Landing page & emergency hotline
│   ├── login.html                            # Authentication portal (with 1-click demo logins)
│   ├── register.html                         # Customer account registration
│   ├── customer/                             # Customer portal (8 pages)
│   │   ├── dashboard.html, vehicles.html, add-vehicle.html, book-service.html,
│   │   ├── service-history.html, breakdown.html, track-request.html, reminders.html
│   ├── admin/                                # Admin portal (7 pages)
│   │   ├── dashboard.html, customers.html, vehicles.html, service-requests.html,
│   │   ├── breakdown-requests.html, mechanics.html, charges.html
│   ├── mechanic/                             # Field mechanic portal (3 pages)
│   │   ├── dashboard.html, assigned-jobs.html, update-job.html
│   ├── css/style.css                         # Dark/Light theme, responsive cards, status badges
│   └── js/                                   # Frontend API client modules
│       ├── main.js, auth.js, customer.js, admin.js, mechanic.js
│
└── src/
    ├── main/
    │   ├── java/com/svsbas/                  # 86 backend classes across 9 domain modules
    │   └── resources/application.yml         # Dual profile configuration (MySQL + H2)
    └── test/java/com/svsbas/                 # JUnit 5 automated unit test suites
```

---

## 4. Default Demo Accounts

| Role | Email | Password | Pre-seeded Resources |
|---|---|---|---|
| **Customer** | `customer@demo.com` | `password123` | 2 vehicles: Creta (`KA-01-MJ-5021`), Nexon EV (`KA-05-EV-9912`) |
| **Mechanic** | `mechanic@demo.com` | `password123` | Active mechanic profile with live GPS coordinates |
| **Admin** | `admin@demo.com` | `password123` | Full access to central metrics, dispatch, and tariffs |

---

## 5. How to Run the Project

### Option A: Both Servers (Frontend & Backend)
1. **Start the Backend**:
   ```powershell
   & "d:\Project1\maven\bin\mvn.cmd" spring-boot:run "-Dspring-boot.run.profiles=h2"
   ```
2. **Start the Frontend**:
   ```powershell
   powershell -ExecutionPolicy Bypass -File "d:\Project1\frontend\start-server.ps1" -Port 5500
   ```
3. **Open the Web Browser**:
   - **Frontend**: `http://localhost:5500/`
   - **Swagger UI**: `http://localhost:8080/swagger-ui.html`
   - **API Specs**: `http://localhost:8080/api-docs`

---

## 6. Automated Testing

### Maven Unit Tests
```powershell
& "d:\Project1\maven\bin\mvn.cmd" test
```
All unit tests in `CostCalculationServiceTest` and `GeoLocationUtilTest` pass with 0 failures and 0 errors.

### Postman API Testing
1. Import `svsbas_postman_collection.json` into Postman.
2. Import `svsbas_postman_environment.json`.
3. Click **Run Collection** to execute tests with token handshakes and status assertions.
