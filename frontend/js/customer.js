/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Customer Operations JavaScript (customer.js)
 * Fully connected to Spring Boot REST API
 */

const CustomerModule = {
  // Fallback Mock Data Store (used only if offline/mock enabled)
  mockVehicles: [
    {
      id: 1,
      regNumber: 'KA-01-MJ-5021',
      brand: 'Hyundai',
      model: 'Creta SX (O)',
      fuelType: 'PETROL',
      manufactureYear: 2022,
      currentMileage: 28500
    },
    {
      id: 2,
      regNumber: 'KA-05-EV-9912',
      brand: 'Tata',
      model: 'Nexon EV Max',
      fuelType: 'ELECTRIC',
      manufactureYear: 2023,
      currentMileage: 14200
    }
  ],

  mockBookings: [],
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

  async init() {
    await this.loadVehicles();
    await this.populateVehicleSelects();
    await this.loadBookings();
    this.loadReminders();
    this.bindAddVehicleForm();
    this.bindBookingForm();
    this.bindSosTrigger();
  },

  /**
   * Load vehicles from the backend and render in the UI
   */
  async loadVehicles() {
    const container = document.getElementById('customerVehicleList');
    if (!container) return;

    try {
      container.innerHTML = `
        <div class="col-12 text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="mt-2 text-muted">Loading your garage from server...</p>
        </div>
      `;

      let vehicles = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const response = await apiRequest('/vehicles');
        vehicles = (response && response.data) ? response.data : [];
      } else {
        vehicles = this.mockVehicles;
      }

      const countEl = document.getElementById('statCustomerVehiclesCount');
      if (countEl) {
        countEl.textContent = vehicles ? vehicles.length : 0;
      }

      if (!vehicles || vehicles.length === 0) {
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

      container.innerHTML = vehicles.map(v => {
        const mileage = v.currentMileage != null ? v.currentMileage : (v.mileage || 0);
        return `
          <div class="col-md-6 col-lg-4 mb-4">
            <div class="card-custom h-100 p-3 card-interactive shadow-sm border">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <span class="badge bg-light text-dark border px-2 py-1 font-monospace fw-bold fs-6">${v.regNumber}</span>
                <span class="badge bg-primary-subtle text-primary fw-semibold">${v.fuelType}</span>
              </div>
              <h5 class="fw-bold mb-1">${v.brand} ${v.model}</h5>
              <p class="text-secondary small mb-3">Model Year: ${v.manufactureYear} | Mileage: ${mileage.toLocaleString()} km</p>
              <div class="border-top pt-2 mt-auto d-flex justify-content-between align-items-center">
                <small class="text-muted"><i class="far fa-user me-1"></i>${v.ownerName || 'Self'}</small>
                <div>
                  <a href="book-service.html?vehicleId=${v.id}" class="btn btn-sm btn-outline-primary me-1"><i class="fas fa-tools"></i> Book</a>
                  <button class="btn btn-sm btn-outline-danger" onclick="CustomerModule.deleteVehicle(${v.id})"><i class="fas fa-trash"></i></button>
                </div>
              </div>
            </div>
          </div>
        `;
      }).join('');
    } catch (err) {
      console.error('Failed to load vehicles from server', err);
      container.innerHTML = `
        <div class="col-12 alert alert-danger">
          <i class="fas fa-exclamation-triangle me-2"></i>Failed to load vehicles: ${err.message || 'Server connection error'}
        </div>
      `;
    }
  },

  /**
   * Delete vehicle from garage
   */
  async deleteVehicle(id) {
    if (!confirm('Are you sure you want to remove this vehicle from your garage?')) return;
    try {
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        await apiRequest(`/vehicles/${id}`, 'DELETE');
        Toast.success('Vehicle removed successfully from database');
      } else {
        this.mockVehicles = this.mockVehicles.filter(v => v.id !== id);
        Toast.success('Vehicle removed successfully');
      }
      await this.loadVehicles();
    } catch (err) {
      console.error('Failed to delete vehicle', err);
      Toast.error(err.message || 'Could not delete vehicle');
    }
  },

  /**
   * Populate vehicle dropdowns across booking and SOS forms
   */
  async populateVehicleSelects() {
    const selects = [
      document.getElementById('bookingVehicleSelect'),
      document.getElementById('sosVehicleSelect')
    ].filter(Boolean);

    if (selects.length === 0) return;

    try {
      let vehicles = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/vehicles');
        vehicles = (res && res.data) ? res.data : [];
      } else {
        vehicles = this.mockVehicles;
      }

      if (vehicles.length > 0) {
        selects.forEach(select => {
          select.innerHTML = vehicles.map(v => `
            <option value="${v.id}">${v.regNumber} - ${v.brand} ${v.model} [${v.fuelType}]</option>
          `).join('');
        });
      }
    } catch (e) {
      console.warn('Could not populate vehicle selects', e);
    }
  },

  /**
   * Load regular service bookings
   */
  async loadBookings() {
    const tableBody = document.getElementById('customerBookingsTableBody');
    if (!tableBody) return;

    try {
      let bookings = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/bookings');
        bookings = (res && res.data) ? res.data : [];
      } else {
        bookings = this.mockBookings;
      }

      if (!bookings || bookings.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">No service bookings found.</td></tr>`;
        return;
      }

      tableBody.innerHTML = bookings.map(b => `
        <tr>
          <td class="fw-bold font-monospace">${b.bookingRef}</td>
          <td>
            <div class="fw-semibold">${b.vehicleInfo || b.vehicleModel || 'Vehicle'}</div>
            <small class="text-muted font-monospace">${b.regNumber || ''}</small>
          </td>
          <td>
            <small class="text-dark">${(b.services && b.services.length) ? b.services.join(', ') : 'Standard Maintenance'}</small>
          </td>
          <td>${b.preferredSlot ? new Date(b.preferredSlot).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' }) : 'Scheduled'}</td>
          <td>${b.mechanicName || '<span class="text-muted fst-italic">Unassigned</span>'}</td>
          <td>${getStatusBadge(b.status)}</td>
          <td class="fw-bold text-end">${formatCurrency(b.estimatedCost || 0)}</td>
        </tr>
      `).join('');
    } catch (err) {
      console.error('Failed to load bookings', err);
    }
  },

  /**
   * Render maintenance reminders
   */
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

  /**
   * Bind Add Vehicle form submission to Spring Boot REST API
   */
  bindAddVehicleForm() {
    const form = document.getElementById('addVehicleForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const submitBtn = form.querySelector('button[type="submit"]');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Registering Vehicle...';
      }

      const vehicleData = {
        regNumber: document.getElementById('vehRegNumber').value.trim().toUpperCase(),
        brand: document.getElementById('vehBrand').value.trim(),
        model: document.getElementById('vehModel').value.trim(),
        fuelType: document.getElementById('vehFuelType').value,
        manufactureYear: parseInt(document.getElementById('vehYear').value, 10),
        currentMileage: parseInt(document.getElementById('vehMileage').value, 10)
      };

      try {
        if (!SVS_CONFIG.USE_MOCK_DATA) {
          const res = await apiRequest('/vehicles', 'POST', vehicleData);
          Toast.success(`Vehicle ${res.data.regNumber} registered successfully in database!`);
        } else {
          this.mockVehicles.push({ id: Date.now(), ...vehicleData });
          Toast.success(`Vehicle ${vehicleData.regNumber} added to garage!`);
        }

        setTimeout(() => {
          window.location.href = 'vehicles.html';
        }, 700);
      } catch (err) {
        console.error('Failed to register vehicle', err);
        const errMsg = (err && (err.message || err.error)) ? (err.message || err.error) : 'Failed to register vehicle. Please check inputs.';
        Toast.error(errMsg);
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.innerHTML = '<i class="fas fa-check me-1"></i> Save to Garage';
        }
      }
    });
  },

  /**
   * Bind Booking form submission to Spring Boot REST API
   */
  bindBookingForm() {
    const form = document.getElementById('bookServiceForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const vehicleSelect = document.getElementById('bookingVehicleSelect');
      const vehicleId = parseInt(vehicleSelect.value, 10);
      const slotDate = document.getElementById('bookingSlotDate').value;
      const slotTime = document.getElementById('bookingSlotTime').value;
      const notes = (document.getElementById('bookingNotes') || {}).value || '';

      const submitBtn = form.querySelector('button[type="submit"]');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Confirming Appointment...';
      }

      try {
        if (!SVS_CONFIG.USE_MOCK_DATA) {
          const payload = {
            vehicleId: vehicleId,
            serviceCatalogIds: [1], // Comprehensive Periodic Maintenance
            preferredSlot: `${slotDate}T${slotTime}:00`,
            customerNotes: notes
          };
          const res = await apiRequest('/bookings', 'POST', payload);
          Toast.success(`Service slot booked! Ref: ${res.data.bookingRef}`);
        } else {
          Toast.success('Service slot booked successfully! (Mock)');
        }

        setTimeout(() => {
          window.location.href = 'service-history.html';
        }, 800);
      } catch (err) {
        console.error('Failed to book service', err);
        Toast.error(err.message || 'Failed to book service appointment');
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.innerHTML = '<i class="fas fa-calendar-check me-2"></i> Confirm Booking';
        }
      }
    });
  },

  /**
   * Bind Emergency SOS trigger to Spring Boot REST API
   */
  bindSosTrigger() {
    const btn = document.getElementById('btnTriggerSos');
    if (!btn) return;

    btn.addEventListener('click', async () => {
      const vehicleSelect = document.getElementById('sosVehicleSelect');
      const typeSelect = document.getElementById('sosTypeSelect');
      const addressInput = document.getElementById('sosAddressInput');

      const vehicleId = vehicleSelect ? parseInt(vehicleSelect.value, 10) : 1;
      const breakdownType = typeSelect ? typeSelect.value : 'FLAT_TYRE';
      const address = addressInput && addressInput.value.trim() ? addressInput.value.trim() : 'Current GPS Location';

      btn.disabled = true;
      btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Dispatching Rapid Response...';

      let lat = 12.9562;
      let lon = 77.7019;

      if (navigator.geolocation) {
        try {
          const pos = await new Promise((res, rej) => navigator.geolocation.getCurrentPosition(res, rej, { timeout: 3000 }));
          lat = pos.coords.latitude;
          lon = pos.coords.longitude;
        } catch (e) {
          console.warn('Geolocation fallback used', e);
        }
      }

      try {
        if (!SVS_CONFIG.USE_MOCK_DATA) {
          const payload = {
            vehicleId,
            breakdownType,
            latitude: lat,
            longitude: lon,
            locationAddress: address
          };
          const res = await apiRequest('/breakdowns/sos', 'POST', payload);
          Toast.success(`Emergency SOS dispatched! Ref: ${res.data.sosRef}`);
        } else {
          Toast.success('Emergency SOS dispatched! (Mock)');
        }

        setTimeout(() => {
          window.location.href = 'track-request.html';
        }, 1000);
      } catch (err) {
        console.error('Failed to trigger SOS', err);
        Toast.error(err.message || 'Failed to dispatch SOS alert');
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-satellite-dish me-2"></i> Send Emergency SOS Alert';
      }
    });
  }
};

// Auto-initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  CustomerModule.init();
});
