/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Customer Operations JavaScript (customer.js)
 */

const CustomerModule = {
  // Mock Data Store for Customer Context
  mockVehicles: [
    {
      id: 1,
      regNumber: 'KA-01-MJ-5021',
      brand: 'Hyundai',
      model: 'Creta SX (O)',
      fuelType: 'PETROL',
      manufactureYear: 2022,
      mileage: 28500,
      lastServiceDate: '2026-03-10'
    },
    {
      id: 2,
      regNumber: 'KA-05-EV-9912',
      brand: 'Tata',
      model: 'Nexon EV Max',
      fuelType: 'ELECTRIC',
      manufactureYear: 2023,
      mileage: 14200,
      lastServiceDate: '2026-05-18'
    }
  ],

  mockBookings: [
    {
      id: 101,
      bookingRef: 'SB-2026-0819',
      regNumber: 'KA-01-MJ-5021',
      vehicleModel: 'Hyundai Creta',
      services: ['General Periodic Maintenance', 'Engine Oil & Filter Change', 'Wheel Balancing'],
      preferredSlot: '2026-09-08 10:30 AM',
      mechanicName: 'Vikram Singh',
      status: 'ASSIGNED',
      estimatedCost: 4850.00
    },
    {
      id: 98,
      bookingRef: 'SB-2026-0518',
      regNumber: 'KA-05-EV-9912',
      vehicleModel: 'Tata Nexon EV Max',
      services: ['High Voltage Battery Check', 'Brake Fluid Flush'],
      preferredSlot: '2026-05-18 02:00 PM',
      mechanicName: 'Suresh Kumar',
      status: 'COMPLETED',
      estimatedCost: 3200.00,
      finalCost: 3200.00
    }
  ],

  mockBreakdowns: [
    {
      id: 201,
      sosRef: 'SOS-2026-0902-881',
      regNumber: 'KA-01-MJ-5021',
      breakdownType: 'FLAT_TYRE',
      location: 'Outer Ring Road, Near Marathahalli Bridge, Bengaluru',
      latitude: 12.9562,
      longitude: 77.7019,
      status: 'MECHANIC_EN_ROUTE',
      mechanicName: 'Vikram Singh',
      mechanicPhone: '+91 91234 56789',
      estimatedEtaMinutes: 14,
      createdAt: '2026-09-02 21:15'
    }
  ],

  mockReminders: [
    {
      id: 1,
      regNumber: 'KA-01-MJ-5021',
      vehicleModel: 'Hyundai Creta',
      title: 'Scheduled 30,000 KM Periodic Maintenance Due',
      dueDate: '2026-09-15',
      priority: 'HIGH',
      description: 'Engine oil replacement, brake pad inspection, and wheel alignment recommended.'
    },
    {
      id: 2,
      regNumber: 'KA-05-EV-9912',
      vehicleModel: 'Tata Nexon EV Max',
      title: 'Annual Battery Health & Diagnostic Check',
      dueDate: '2026-11-20',
      priority: 'MEDIUM',
      description: 'Yearly EV diagnostic scan and electrical cooling loop inspection.'
    }
  ],

  init() {
    this.loadVehicles();
    this.loadBookings();
    this.loadReminders();
    this.bindAddVehicleForm();
    this.bindBookingForm();
    this.bindSosTrigger();
  },

  loadVehicles() {
    const container = document.getElementById('customerVehicleList');
    if (!container) return;

    if (this.mockVehicles.length === 0) {
      container.innerHTML = `
        <div class="col-12 empty-state-box">
          <div class="empty-state-icon"><i class="fas fa-car-side"></i></div>
          <h5 class="empty-state-title">No Vehicles Registered</h5>
          <p class="empty-state-desc">Add your first vehicle to schedule regular maintenance slots and enable 1-tap SOS assistance.</p>
          <a href="add-vehicle.html" class="btn btn-brand-primary"><i class="fas fa-plus me-2"></i>Add Vehicle</a>
        </div>
      `;
      return;
    }

    container.innerHTML = this.mockVehicles.map(v => `
      <div class="col-md-6 col-lg-4 mb-4">
        <div class="card-custom h-100 p-3 card-interactive">
          <div class="d-flex justify-content-between align-items-start mb-2">
            <span class="badge bg-light text-dark border px-2 py-1 font-monospace fw-bold">${v.regNumber}</span>
            <span class="badge bg-primary-subtle text-primary fw-semibold">${v.fuelType}</span>
          </div>
          <h5 class="fw-bold mb-1">${v.brand} ${v.model}</h5>
          <p class="text-secondary small mb-3">Model Year: ${v.manufactureYear} | Mileage: ${v.mileage.toLocaleString()} km</p>
          <div class="border-top pt-2 mt-auto d-flex justify-content-between align-items-center">
            <small class="text-muted"><i class="far fa-calendar-alt me-1"></i>Last Service: ${v.lastServiceDate || 'None'}</small>
            <div>
              <a href="book-service.html?vehicleId=${v.id}" class="btn btn-sm btn-outline-primary me-1"><i class="fas fa-tools"></i> Book</a>
              <button class="btn btn-sm btn-outline-danger" onclick="CustomerModule.deleteVehicle(${v.id})"><i class="fas fa-trash"></i></button>
            </div>
          </div>
        </div>
      </div>
    `).join('');
  },

  deleteVehicle(id) {
    if (!confirm('Are you sure you want to remove this vehicle from your garage?')) return;
    this.mockVehicles = this.mockVehicles.filter(v => v.id !== id);
    Toast.success('Vehicle removed successfully');
    this.loadVehicles();
  },

  loadBookings() {
    const tableBody = document.getElementById('customerBookingsTableBody');
    if (!tableBody) return;

    if (this.mockBookings.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">No service bookings found.</td></tr>`;
      return;
    }

    tableBody.innerHTML = this.mockBookings.map(b => `
      <tr>
        <td class="fw-bold font-monospace">${b.bookingRef}</td>
        <td>
          <div class="fw-semibold">${b.vehicleModel}</div>
          <small class="text-muted font-monospace">${b.regNumber}</small>
        </td>
        <td>
          <small class="text-dark">${b.services.join(', ')}</small>
        </td>
        <td>${b.preferredSlot}</td>
        <td>${b.mechanicName || '<span class="text-muted italic">Unassigned</span>'}</td>
        <td>${getStatusBadge(b.status)}</td>
        <td class="fw-bold text-end">${formatCurrency(b.finalCost || b.estimatedCost)}</td>
      </tr>
    `).join('');
  },

  loadReminders() {
    const container = document.getElementById('remindersListContainer');
    if (!container) return;

    container.innerHTML = this.mockReminders.map(r => `
      <div class="card-custom p-3 mb-3 border-start border-4 ${r.priority === 'HIGH' ? 'border-danger' : 'border-warning'}">
        <div class="d-flex justify-content-between align-items-center mb-1">
          <h6 class="fw-bold mb-0 text-dark">${r.title}</h6>
          <span class="badge ${r.priority === 'HIGH' ? 'bg-danger-subtle text-danger' : 'bg-warning-subtle text-warning'}">${r.priority} PRIORITY</span>
        </div>
        <p class="text-secondary small mb-2">${r.description}</p>
        <div class="d-flex justify-content-between align-items-center">
          <small class="text-muted font-monospace"><i class="fas fa-car me-1"></i>${r.regNumber} (${r.vehicleModel})</small>
          <a href="book-service.html?reg=${r.regNumber}" class="btn btn-sm btn-brand-primary">Schedule Service</a>
        </div>
      </div>
    `).join('');
  },

  bindAddVehicleForm() {
    const form = document.getElementById('addVehicleForm');
    if (!form) return;

    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const newV = {
        id: Date.now(),
        regNumber: document.getElementById('vehRegNumber').value.trim().toUpperCase(),
        brand: document.getElementById('vehBrand').value.trim(),
        model: document.getElementById('vehModel').value.trim(),
        fuelType: document.getElementById('vehFuelType').value,
        manufactureYear: parseInt(document.getElementById('vehYear').value, 10),
        mileage: parseInt(document.getElementById('vehMileage').value, 10),
        lastServiceDate: 'None'
      };

      this.mockVehicles.push(newV);
      Toast.success(`Vehicle ${newV.regNumber} added to garage!`);
      setTimeout(() => {
        window.location.href = 'vehicles.html';
      }, 700);
    });
  },

  bindBookingForm() {
    const form = document.getElementById('bookServiceForm');
    if (!form) return;

    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const vehicleSelect = document.getElementById('bookingVehicleSelect');
      const selectedVehicleText = vehicleSelect.options[vehicleSelect.selectedIndex].text;
      
      const newBooking = {
        id: Date.now(),
        bookingRef: 'SB-2026-' + Math.floor(1000 + Math.random() * 9000),
        regNumber: selectedVehicleText.split(' - ')[0] || 'KA-NEW',
        vehicleModel: selectedVehicleText.split(' - ')[1] || 'Vehicle',
        services: ['General Maintenance & Diagnostics'],
        preferredSlot: document.getElementById('bookingSlotDate').value + ' ' + document.getElementById('bookingSlotTime').value,
        mechanicName: null,
        status: 'REQUESTED',
        estimatedCost: 3500.00
      };

      this.mockBookings.unshift(newBooking);
      Toast.success('Service slot booked successfully! Admin will assign a certified mechanic.');
      setTimeout(() => {
        window.location.href = 'service-history.html';
      }, 800);
    });
  },

  bindSosTrigger() {
    const btn = document.getElementById('btnTriggerSos');
    if (!btn) return;

    btn.addEventListener('click', () => {
      if (!confirm('EMERGENCY SOS: This will alert nearest field mechanics and dispatch roadside assistance. Proceed?')) return;

      btn.disabled = true;
      btn.innerHTML = '<i class="fas fa-circle-notch fa-spin me-2"></i>Acquiring Precise GPS...';

      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (pos) => {
            const coords = { lat: pos.coords.latitude, lng: pos.coords.longitude };
            CustomerModule.createSosRequest(coords);
          },
          (err) => {
            console.warn('Geolocation failed or denied, using fallback city coordinates', err);
            CustomerModule.createSosRequest({ lat: 12.9716, lng: 77.5946 }); // Default Bangalore Center
          },
          { enableHighAccuracy: true, timeout: 8000 }
        );
      } else {
        CustomerModule.createSosRequest({ lat: 12.9716, lng: 77.5946 });
      }
    });
  },

  createSosRequest(coords) {
    const newSos = {
      id: Date.now(),
      sosRef: 'SOS-2026-' + Math.floor(1000 + Math.random() * 9000),
      regNumber: 'KA-01-MJ-5021',
      breakdownType: 'CRITICAL_ASSISTANCE',
      location: `GPS: ${coords.lat.toFixed(4)}° N, ${coords.lng.toFixed(4)}° E (Auto-located)`,
      latitude: coords.lat,
      longitude: coords.lng,
      status: 'DISPATCHED',
      mechanicName: 'Vikram Singh (Nearest Unit)',
      mechanicPhone: '+91 91234 56789',
      estimatedEtaMinutes: 12,
      createdAt: 'Just now'
    };

    this.mockBreakdowns.unshift(newSos);
    Toast.success('SOS Alert Dispatched! Nearest mechanic has been notified.');
    setTimeout(() => {
      window.location.href = 'track-request.html?sosId=' + newSos.id;
    }, 1000);
  }
};

document.addEventListener('DOMContentLoaded', () => {
  CustomerModule.init();
});
