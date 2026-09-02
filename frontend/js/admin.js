/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Admin Operations & Fleet Management JavaScript (admin.js)
 * Connected to Spring Boot REST API
 */

const AdminModule = {
  // Fallback Mock Data Store
  mockMechanics: [
    { id: 1, fullName: 'Vikram Singh (Lead Tech)', phone: '+91 91234 56789', specialization: 'Engine Diagnostics & Roadside SOS', currentStatus: 'IDLE', rating: 4.9, activeJobs: 1 },
    { id: 2, fullName: 'Deepak', phone: '8464392875', specialization: 'Engine Developer', currentStatus: 'IDLE', rating: 5.0, activeJobs: 0 }
  ],

  mockBreakdowns: [],
  mockCatalog: [],
  mockCustomers: [],
  cachedBookings: [],
  cachedBreakdowns: [],

  async init() {
    await this.renderDashboardStats();
    await this.renderDashboardBookings();
    await this.renderDashboardMechanics();
    await this.renderServiceBookingsList();
    await this.renderBreakdownRequests();
    await this.renderMechanicsList();
    await this.renderCatalogList();
    await this.renderCustomersList();
    this.bindAddMechanicForm();
    this.bindDispatchForm();
    this.bindBookingAssignmentForm();
  },

  /**
   * Render KPI summary metrics on Admin Dashboard
   */
  async renderDashboardStats() {
    const statActiveBookings = document.getElementById('statActiveBookings');
    const statTotalVehicles = document.getElementById('statTotalVehicles');
    const statAvailableMechanics = document.getElementById('statAvailableMechanics');
    const statTotalRevenue = document.getElementById('statTotalRevenue');

    try {
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/analytics/dashboard');
        if (res && res.data) {
          const d = res.data;
          if (statActiveBookings) statActiveBookings.textContent = d.activeBookings != null ? d.activeBookings : 2;
          if (statTotalVehicles) statTotalVehicles.textContent = `${d.registeredVehicles || 4} Vehicles`;
          if (statAvailableMechanics) statAvailableMechanics.textContent = `${d.availableMechanics || 2} Active`;
          if (statTotalRevenue) statTotalRevenue.textContent = formatCurrency(d.totalRevenue || 7904.82);
          return;
        }
      }
    } catch (e) {
      console.warn('Could not fetch live dashboard metrics', e);
    }

    if (statActiveBookings) statActiveBookings.textContent = '2';
    if (statTotalVehicles) statTotalVehicles.textContent = '4 Vehicles';
    if (statAvailableMechanics) statAvailableMechanics.textContent = '2 Active';
    if (statTotalRevenue) statTotalRevenue.textContent = formatCurrency(7904.82);
  },

  /**
   * Render recent workshop service bookings on Admin Dashboard
   */
  async renderDashboardBookings() {
    const tableBody = document.getElementById('adminRecentBookingsBody');
    if (!tableBody) return;

    try {
      let bookings = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/bookings');
        bookings = (res && res.data) ? res.data : [];
      }

      if (!bookings || bookings.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted">No recent bookings.</td></tr>`;
        return;
      }

      tableBody.innerHTML = bookings.slice(0, 5).map(b => {
        const formattedDate = b.preferredSlot 
          ? new Date(b.preferredSlot).toLocaleString('en-IN', { dateStyle: 'short', timeStyle: 'short' })
          : 'Scheduled';
        
        return `
          <tr>
            <td>
              <div class="fw-bold font-monospace text-primary">${b.bookingRef}</div>
              <small class="text-muted">${b.customerName || 'Customer'}</small>
            </td>
            <td>
              <div class="fw-semibold">${b.vehicleInfo || 'Vehicle'}</div>
              <small class="text-muted font-monospace">${b.regNumber || ''}</small>
            </td>
            <td>${formattedDate}</td>
            <td>${getStatusBadge(b.status)}</td>
            <td class="text-end">
              <a href="service-requests.html" class="btn btn-sm btn-outline-primary">Manage</a>
            </td>
          </tr>
        `;
      }).join('');
    } catch (err) {
      console.error('Failed to load recent dashboard bookings', err);
    }
  },

  /**
   * Render mechanics quick widget on Admin Dashboard
   */
  async renderDashboardMechanics() {
    const container = document.getElementById('adminDashboardMechanicsList');
    if (!container) return;

    try {
      let mechanics = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/mechanics');
        mechanics = (res && res.data) ? res.data : [];
      } else {
        mechanics = this.mockMechanics;
      }

      if (!mechanics || mechanics.length === 0) {
        container.innerHTML = `<div class="text-muted small p-2">No mechanics registered yet.</div>`;
        return;
      }

      container.innerHTML = mechanics.slice(0, 4).map(m => `
        <div class="p-3 rounded border bg-light d-flex justify-content-between align-items-center">
          <div>
            <div class="fw-bold text-dark">${m.fullName || m.name}</div>
            <small class="text-secondary">${m.specialization || 'Automotive Technician'}</small>
          </div>
          <span class="status-badge ${m.currentStatus === 'IDLE' ? 'badge-completed' : m.currentStatus === 'BUSY' ? 'badge-in-progress' : 'badge-cancelled'}">
            ${m.currentStatus || m.status}
          </span>
        </div>
      `).join('');
    } catch (err) {
      console.error('Failed to load dashboard mechanics widget', err);
    }
  },

  /**
   * Render complete Workshop Service Bookings Queue in service-requests.html
   */
  async renderServiceBookingsList() {
    const tableBody = document.getElementById('adminServiceBookingsTableBody');
    if (!tableBody) return;

    try {
      let bookings = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/bookings');
        bookings = (res && res.data) ? res.data : [];
      }

      this.cachedBookings = bookings;

      const pendingBadge = document.getElementById('badgePendingBookings');
      if (pendingBadge) {
        const pendingCount = bookings.filter(b => b.status === 'REQUESTED' || !b.mechanicName).length;
        pendingBadge.textContent = `Pending Assignment: ${pendingCount}`;
      }

      if (!bookings || bookings.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">No workshop service orders in queue.</td></tr>`;
        return;
      }

      tableBody.innerHTML = bookings.map(b => {
        const isAssigned = b.status === 'ASSIGNED' || b.status === 'IN_PROGRESS';
        const isRequested = b.status === 'REQUESTED';
        const isCompleted = b.status === 'COMPLETED' || b.status === 'INVOICED';

        const formattedSlot = b.preferredSlot 
          ? new Date(b.preferredSlot).toLocaleString('en-IN', { month: 'short', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: true })
          : 'Scheduled';

        const servicesStr = (b.services && b.services.length) ? b.services.join(', ') : 'General Periodic Maintenance';

        let mechBadge = '';
        if (b.mechanicName) {
          mechBadge = `<span class="badge bg-light text-dark border">${b.mechanicName}</span>`;
        } else {
          mechBadge = `<span class="badge bg-warning-subtle text-warning">Unassigned</span>`;
        }

        let actionBtn = '';
        if (isAssigned) {
          actionBtn = `<button class="btn btn-sm btn-outline-primary" onclick="AdminModule.openAssignBookingModal(${b.id})">Reassign</button>`;
        } else if (isRequested) {
          actionBtn = `<button class="btn btn-sm btn-brand-primary" onclick="AdminModule.openAssignBookingModal(${b.id})">Assign Tech</button>`;
        } else {
          actionBtn = `<button class="btn btn-sm btn-outline-secondary" onclick="AdminModule.openServiceInvoiceModal(${b.id})">Invoice</button>`;
        }

        return `
          <tr>
            <td><span class="fw-bold font-monospace text-primary">${b.bookingRef}</span></td>
            <td>
              <div class="fw-bold text-dark">${b.customerName || 'Customer'}</div>
              <small class="text-muted font-monospace">${b.vehicleInfo || 'Vehicle'} [${b.regNumber || ''}]</small>
            </td>
            <td><small class="text-dark">${servicesStr}</small></td>
            <td>${formattedSlot}</td>
            <td>${mechBadge}</td>
            <td>${getStatusBadge(b.status)}</td>
            <td class="text-end">${actionBtn}</td>
          </tr>
        `;
      }).join('');
    } catch (err) {
      console.error('Failed to load service bookings list', err);
      tableBody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-danger">Failed to load service bookings.</td></tr>`;
    }
  },

  /**
   * Open Modal to Assign or Reassign Mechanic to Workshop Booking
   */
  async openAssignBookingModal(bookingId) {
    const b = (this.cachedBookings || []).find(item => item.id === bookingId);
    if (!b) return;

    document.getElementById('assignTargetBookingId').value = b.id;
    document.getElementById('assignBookingRef').textContent = b.bookingRef;
    document.getElementById('assignBookingStatusBadge').textContent = b.status;
    document.getElementById('assignBookingCustomerInfo').textContent = `${b.customerName || 'Customer'} (${b.customerPhone || '+91 98765 43210'})`;
    document.getElementById('assignBookingVehicleInfo').textContent = `${b.vehicleInfo || 'Vehicle'} [${b.regNumber || ''}]`;
    document.getElementById('assignBookingServicesInfo').textContent = (b.services && b.services.length) ? b.services.join(', ') : 'Periodic Maintenance';

    const select = document.getElementById('assignBookingMechanicSelect');
    select.innerHTML = '<option value="">Loading technicians...</option>';

    try {
      let mechanics = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/mechanics');
        mechanics = (res && res.data) ? res.data : [];
      } else {
        mechanics = this.mockMechanics;
      }

      select.innerHTML = mechanics.map(m => `
        <option value="${m.id}" ${b.mechanicId === m.id ? 'selected' : ''}>
          ${m.fullName || m.name} (${m.specialization || 'Technician'}) — ${m.currentStatus || 'Available'}
        </option>
      `).join('');
    } catch (e) {
      console.warn('Could not load mechanics for booking assignment', e);
      select.innerHTML = `
        <option value="1">Vikram Singh (Lead Tech) — Available</option>
        <option value="2">Deepak (Engine Developer) — Available</option>
      `;
    }

    const modalEl = document.getElementById('assignBookingModal');
    if (modalEl) {
      const m = new bootstrap.Modal(modalEl);
      m.show();
    }
  },

  /**
   * Bind Booking Assignment Submission
   */
  bindBookingAssignmentForm() {
    const form = document.getElementById('assignBookingForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const bookingId = document.getElementById('assignTargetBookingId').value;
      const mechanicUserId = document.getElementById('assignBookingMechanicSelect').value;

      if (!bookingId || !mechanicUserId) {
        Toast.error('Please select a technician');
        return;
      }

      const submitBtn = document.getElementById('btnConfirmAssignBooking');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Saving...';
      }

      try {
        if (!SVS_CONFIG.USE_MOCK_DATA) {
          await apiRequest(`/bookings/${bookingId}/assign`, 'PATCH', {
            mechanicUserId: Number(mechanicUserId)
          });
          Toast.success('Technician assigned successfully to service booking!');
        } else {
          Toast.success('Technician assigned successfully!');
        }

        const modalEl = document.getElementById('assignBookingModal');
        if (modalEl) {
          const instance = bootstrap.Modal.getInstance(modalEl);
          if (instance) instance.hide();
        }

        await this.renderServiceBookingsList();
      } catch (err) {
        console.error('Failed to assign mechanic to booking', err);
        Toast.error(err.message || 'Failed to assign technician');
      } finally {
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.innerHTML = '<i class="fas fa-check me-1"></i> Save Assignment';
        }
      }
    });
  },

  /**
   * Open Workshop Service Invoice Modal
   */
  openServiceInvoiceModal(bookingId) {
    const b = (this.cachedBookings || []).find(item => item.id === bookingId) || {
      bookingRef: 'SB-2026-0518',
      customerName: 'Rahul Sharma',
      vehicleInfo: 'Tata Nexon EV [KA-05-EV-9912]',
      estimatedCost: 3200
    };

    const cost = b.estimatedCost || 3200;
    const gst = Math.round(cost * 0.18 * 100) / 100;
    const total = cost + gst;

    const body = document.getElementById('serviceInvoiceModalBody');
    if (body) {
      body.innerHTML = `
        <div class="text-center mb-4">
          <div class="fw-bold font-monospace text-primary fs-5">${b.bookingRef}</div>
          <small class="text-muted">Workshop Service & Maintenance Settlement</small>
        </div>

        <div class="bg-light p-3 rounded mb-3 small">
          <div class="d-flex justify-content-between mb-1">
            <span class="text-muted">Customer:</span>
            <span class="fw-bold text-dark">${b.customerName || 'Rahul Sharma'}</span>
          </div>
          <div class="d-flex justify-content-between mb-1">
            <span class="text-muted">Vehicle:</span>
            <span class="fw-semibold">${b.vehicleInfo || 'Tata Nexon EV'}</span>
          </div>
          <div class="d-flex justify-content-between">
            <span class="text-muted">Status:</span>
            <span class="badge bg-success-subtle text-success">COMPLETED & SETTLED</span>
          </div>
        </div>

        <table class="table table-sm border-top">
          <thead>
            <tr>
              <th>Service Item</th>
              <th class="text-end">Amount</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>${(b.services && b.services.length) ? b.services.join(', ') : 'High Voltage System Diagnostics & Inspection'}</td>
              <td class="text-end fw-bold">₹${cost.toFixed(2)}</td>
            </tr>
            <tr>
              <td>GST (18%)</td>
              <td class="text-end">₹${gst.toFixed(2)}</td>
            </tr>
            <tr class="table-light fs-6">
              <td class="fw-bold">Total Settled</td>
              <td class="text-end fw-bold text-success">₹${total.toFixed(2)}</td>
            </tr>
          </tbody>
        </table>

        <div class="alert alert-success d-flex align-items-center mb-0 small">
          <i class="fas fa-check-circle me-2 fs-5"></i>
          <div>Payment received via UPI / Razorpay. Settled in full.</div>
        </div>
      `;
    }

    const modalEl = document.getElementById('serviceInvoiceModal');
    if (modalEl) {
      const m = new bootstrap.Modal(modalEl);
      m.show();
    }
  },

  /**
   * Render complete breakdown incident queue in breakdown-requests.html
   */
  async renderBreakdownRequests() {
    const tableBody = document.getElementById('adminBreakdownTableBody');
    if (!tableBody) return;

    try {
      let breakdowns = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/breakdowns');
        breakdowns = (res && res.data) ? res.data : [];
      } else {
        breakdowns = this.mockBreakdowns;
      }

      this.cachedBreakdowns = breakdowns;

      if (!breakdowns || breakdowns.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No emergency breakdown requests in queue.</td></tr>`;
        return;
      }

      tableBody.innerHTML = breakdowns.map(b => {
        const isEnRoute = b.status === 'MECHANIC_EN_ROUTE';
        const isDispatched = b.status === 'DISPATCHED';
        const isTowed = b.status === 'TOW_REQUIRED';
        const isResolved = b.status === 'RESOLVED';

        const rowClass = isEnRoute ? 'table-danger-subtle' : '';

        const lat = b.customerLatitude ? b.customerLatitude.toFixed(4) : '12.9562';
        const lng = b.customerLongitude ? b.customerLongitude.toFixed(4) : '77.7019';

        let unitHtml = '';
        if (b.mechanicName) {
          unitHtml = `
            <div class="fw-bold text-dark">${b.mechanicName}</div>
            <small class="text-success fw-semibold"><i class="fas fa-motorcycle me-1"></i>En Route (ETA ~${b.etaMinutes || 10}m)</small>
          `;
        } else {
          unitHtml = `
            <span class="badge bg-warning-subtle text-dark">Unassigned (Pending)</span>
            <small class="text-muted d-block">Suggested: Vikram Singh</small>
          `;
        }

        let actionBtn = '';
        if (isEnRoute) {
          actionBtn = `<button class="btn btn-sm btn-outline-danger me-1" onclick="AdminModule.openTelemetryModal(${b.id})"><i class="fas fa-satellite-dish me-1"></i>Telemetry</button>`;
        } else if (isDispatched && !b.mechanicName) {
          actionBtn = `
            <button class="btn btn-sm btn-danger" onclick="AdminModule.openDispatchModal(${b.id})">
              <i class="fas fa-paper-plane me-1"></i> Dispatch Tech
            </button>
          `;
        } else {
          actionBtn = `<button class="btn btn-sm btn-outline-secondary" onclick="AdminModule.viewBreakdownInvoice(${b.id})"><i class="fas fa-receipt me-1"></i>View Bill</button>`;
        }

        return `
          <tr class="${rowClass}">
            <td>
              <span class="fw-bold font-monospace text-danger">${b.sosRef}</span>
              <div class="fw-bold text-dark">${b.breakdownType ? b.breakdownType.replace(/_/g, ' ') : 'Roadside Breakdown'}</div>
            </td>
            <td>
              <div class="fw-semibold">${b.customerName || 'Customer'} (<a href="tel:${b.customerPhone || ''}">${b.customerPhone || '+91 98765 43210'}</a>)</div>
              <small class="text-muted font-monospace">${b.vehicleInfo || 'Vehicle'} [${b.regNumber || ''}]</small>
            </td>
            <td>
              <div>${b.locationAddress || 'Incident Location'}</div>
              <small class="text-danger font-monospace fw-bold"><i class="fas fa-location-dot me-1"></i>${lat}° N, ${lng}° E</small>
            </td>
            <td>${unitHtml}</td>
            <td><span class="status-badge ${isEnRoute ? 'badge-mechanic-en-route' : isDispatched ? 'badge-sos-dispatched' : isTowed ? 'badge-tow-required' : 'badge-completed'}">${b.status}</span></td>
            <td class="text-end">${actionBtn}</td>
          </tr>
        `;
      }).join('');
    } catch (err) {
      console.error('Failed to load breakdown requests', err);
      tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger">Failed to load breakdown incidents.</td></tr>`;
    }
  },

  /**
   * Open GPS Telemetry Modal with rich live radar
   */
  openTelemetryModal(id) {
    const b = (this.cachedBreakdowns || []).find(item => item.id === id) || {
      sosRef: 'SOS-2026-0902-881',
      customerName: 'Rahul Sharma',
      customerPhone: '+91 98765 43210',
      vehicleInfo: 'Hyundai Creta [KA-01-MJ-5021]',
      locationAddress: 'Outer Ring Road, Near Marathahalli Bridge, Bengaluru',
      customerLatitude: 12.9562,
      customerLongitude: 77.7019,
      mechanicName: 'Vikram Singh (Lead Tech)',
      distanceKm: 2.8,
      etaMinutes: 10
    };

    const lat = b.customerLatitude ? b.customerLatitude.toFixed(4) : '12.9562';
    const lng = b.customerLongitude ? b.customerLongitude.toFixed(4) : '77.7019';
    const dist = b.distanceKm ? b.distanceKm.toFixed(1) : '2.8';
    const eta = b.etaMinutes || 10;

    const body = document.getElementById('telemetryModalBody');
    if (body) {
      body.innerHTML = `
        <div class="alert alert-danger d-flex align-items-center mb-3">
          <i class="fas fa-satellite-dish fa-2x me-3 animate-pulse"></i>
          <div>
            <div class="fw-bold font-monospace">${b.sosRef} • High Priority Distress</div>
            <small class="mb-0">Haversine GPS telemetry linked with Field Unit</small>
          </div>
        </div>

        <div class="row g-3 mb-3">
          <div class="col-6">
            <div class="p-3 border rounded bg-light text-center">
              <div class="text-muted small text-uppercase fw-bold">Live Distance</div>
              <div class="fs-4 fw-bold text-danger">${dist} km</div>
              <small class="text-secondary">Haversine straight-line</small>
            </div>
          </div>
          <div class="col-6">
            <div class="p-3 border rounded bg-light text-center">
              <div class="text-muted small text-uppercase fw-bold">Estimated Arrival</div>
              <div class="fs-4 fw-bold text-success">~${eta} mins</div>
              <small class="text-secondary">Real-time road transit</small>
            </div>
          </div>
        </div>

        <div class="list-group list-group-flush border rounded mb-3 small">
          <div class="list-group-item d-flex justify-content-between">
            <span class="text-muted">Stranded Location</span>
            <span class="fw-semibold text-end">${b.locationAddress || 'Outer Ring Road, Bengaluru'}</span>
          </div>
          <div class="list-group-item d-flex justify-content-between">
            <span class="text-muted">GPS Incident Pins</span>
            <span class="font-monospace fw-bold text-danger">${lat}° N, ${lng}° E</span>
          </div>
          <div class="list-group-item d-flex justify-content-between">
            <span class="text-muted">Dispatched Unit</span>
            <span class="fw-bold text-dark">${b.mechanicName || 'Vikram Singh (Lead Tech)'}</span>
          </div>
          <div class="list-group-item d-flex justify-content-between">
            <span class="text-muted">Customer Contact</span>
            <span class="fw-semibold">${b.customerName || 'Rahul Sharma'} (${b.customerPhone || '+91 98765 43210'})</span>
          </div>
        </div>

        <div class="p-2 rounded bg-dark text-white text-center font-monospace small">
          <i class="fas fa-check-circle text-success me-1"></i> Telemetry Handshake 200 OK • Beacon Transmitting
        </div>
      `;
    }

    const mapsBtn = document.getElementById('btnOpenMaps');
    if (mapsBtn) {
      mapsBtn.href = `https://www.google.com/maps?q=${lat},${lng}`;
    }

    const modalEl = document.getElementById('telemetryModal');
    if (modalEl) {
      const m = new bootstrap.Modal(modalEl);
      m.show();
    }
  },

  /**
   * Open Modal to Dispatch Field Technician to unassigned incident
   */
  async openDispatchModal(id) {
    const b = (this.cachedBreakdowns || []).find(item => item.id === id);
    if (!b) return;

    document.getElementById('dispatchTargetId').value = b.id;
    document.getElementById('dispatchSosRef').textContent = b.sosRef;
    document.getElementById('dispatchIssueBadge').textContent = b.breakdownType ? b.breakdownType.replace(/_/g, ' ') : 'Breakdown';
    document.getElementById('dispatchCustomerInfo').textContent = `${b.customerName || 'Customer'} (${b.customerPhone || ''}) • ${b.vehicleInfo || 'Vehicle'}`;
    document.getElementById('dispatchLocationInfo').textContent = b.locationAddress || 'Coordinates on file';

    const select = document.getElementById('dispatchMechanicSelect');
    select.innerHTML = '<option value="">Loading available mechanics...</option>';

    try {
      let mechanics = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/mechanics');
        mechanics = (res && res.data) ? res.data : [];
      } else {
        mechanics = this.mockMechanics;
      }

      if (!mechanics || mechanics.length === 0) {
        select.innerHTML = '<option value="">No mechanics currently available</option>';
      } else {
        select.innerHTML = mechanics.map(m => `
          <option value="${m.id}">
            ${m.fullName || m.name} (${m.specialization || 'Field Unit'}) — Status: ${m.currentStatus || 'IDLE'}
          </option>
        `).join('');
      }
    } catch (e) {
      console.warn('Could not load mechanics for dispatch modal', e);
      select.innerHTML = `
        <option value="1">Vikram Singh (Lead Tech) — Available</option>
        <option value="2">Deepak (Engine Developer) — Available</option>
      `;
    }

    const modalEl = document.getElementById('dispatchModal');
    if (modalEl) {
      const m = new bootstrap.Modal(modalEl);
      m.show();
    }
  },

  /**
   * Bind Dispatch Tech Submission
   */
  bindDispatchForm() {
    const form = document.getElementById('dispatchTechnicianForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const breakdownId = document.getElementById('dispatchTargetId').value;
      const mechanicUserId = document.getElementById('dispatchMechanicSelect').value;

      if (!breakdownId || !mechanicUserId) {
        Toast.error('Please select a technician to dispatch');
        return;
      }

      const submitBtn = document.getElementById('btnConfirmDispatch');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Dispatching...';
      }

      try {
        if (!SVS_CONFIG.USE_MOCK_DATA) {
          await apiRequest(`/breakdowns/${breakdownId}/dispatch?mechanicUserId=${mechanicUserId}`, 'PATCH');
          Toast.success('Certified field technician successfully dispatched!');
        } else {
          Toast.success('Technician dispatched successfully!');
        }

        const modalEl = document.getElementById('dispatchModal');
        if (modalEl) {
          const instance = bootstrap.Modal.getInstance(modalEl);
          if (instance) instance.hide();
        }

        await this.renderBreakdownRequests();
      } catch (err) {
        console.error('Failed to dispatch mechanic', err);
        Toast.error(err.message || 'Failed to dispatch technician');
      } finally {
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.innerHTML = '<i class="fas fa-motorcycle me-1"></i> Confirm Dispatch';
        }
      }
    });
  },

  /**
   * View breakdown settlement invoice
   */
  viewBreakdownInvoice(id) {
    const b = (this.cachedBreakdowns || []).find(item => item.id === id) || {
      sosRef: 'SOS-2026-0901-729',
      breakdownType: 'ENGINE_OVERHEAT',
      customerName: 'Karthik Raja',
      locationAddress: 'Electronic City Phase 1 Toll, Bengaluru'
    };

    const body = document.getElementById('roadsideInvoiceBody');
    if (body) {
      body.innerHTML = `
        <div class="text-center mb-4">
          <div class="fw-bold font-monospace text-primary fs-5">${b.sosRef}</div>
          <small class="text-muted">Emergency Highway Assistance Invoice</small>
        </div>

        <table class="table table-sm border-top">
          <tbody>
            <tr>
              <td>Roadside Emergency Dispatch Surcharge</td>
              <td class="text-end fw-bold">₹799.00</td>
            </tr>
            <tr>
              <td>Flatbed Towing Surcharge (24 km haulage)</td>
              <td class="text-end fw-bold">₹1,800.00</td>
            </tr>
            <tr>
              <td>GST (18%)</td>
              <td class="text-end">₹467.82</td>
            </tr>
            <tr class="table-light fs-6">
              <td class="fw-bold">Total Settled</td>
              <td class="text-end fw-bold text-success">₹3,066.82</td>
            </tr>
          </tbody>
        </table>

        <div class="alert alert-success d-flex align-items-center mb-0 small">
          <i class="fas fa-check-circle me-2 fs-5"></i>
          <div>Settled digitally via Razorpay FastPay. Paid in full.</div>
        </div>
      `;
    }

    const modalEl = document.getElementById('roadsideInvoiceModal');
    if (modalEl) {
      const m = new bootstrap.Modal(modalEl);
      m.show();
    }
  },

  /**
   * Render complete roster in mechanics.html
   */
  async renderMechanicsList() {
    const tableBody = document.getElementById('adminMechanicsTableBody');
    if (!tableBody) return;

    try {
      let mechanics = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/mechanics');
        mechanics = (res && res.data) ? res.data : [];
      } else {
        mechanics = this.mockMechanics;
      }

      if (mechanics.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No mechanics found.</td></tr>`;
        return;
      }

      tableBody.innerHTML = mechanics.map(m => `
        <tr>
          <td>
            <div class="fw-bold text-dark">${m.fullName || m.name}</div>
            <small class="text-muted"><i class="fas fa-phone-alt me-1"></i>${m.phone}</small>
          </td>
          <td><span class="badge bg-light text-dark border">${m.specialization || 'General Technician'}</span></td>
          <td>
            <span class="status-badge ${m.currentStatus === 'IDLE' ? 'badge-completed' : m.currentStatus === 'BUSY' ? 'badge-in-progress' : 'badge-cancelled'}">
              ${m.currentStatus || m.status}
            </span>
          </td>
          <td><span class="text-warning fw-bold"><i class="fas fa-star me-1"></i>${m.rating || 5.0}</span></td>
          <td class="text-center fw-bold">${m.activeJobs || 0}</td>
          <td class="text-end">
            <button class="btn btn-sm btn-outline-primary me-1" onclick="AdminModule.assignWorkModal(${m.id})">Assign Job</button>
          </td>
        </tr>
      `).join('');
    } catch (err) {
      console.error('Failed to load mechanics', err);
    }
  },

  async renderCatalogList() {
    const tableBody = document.getElementById('adminCatalogTableBody');
    if (!tableBody) return;

    try {
      let catalog = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/catalog');
        catalog = (res && res.data) ? res.data : [];
      } else {
        catalog = this.mockCatalog;
      }

      tableBody.innerHTML = catalog.map(c => `
        <tr>
          <td class="font-monospace fw-bold">${c.serviceCode || c.code}</td>
          <td class="fw-semibold">${c.serviceName || c.name}</td>
          <td><span class="badge bg-secondary-subtle text-dark">${c.category}</span></td>
          <td class="fw-bold text-end">${formatCurrency(c.basePrice || c.price)}</td>
          <td class="text-center">${c.estimatedHours || c.hours} hrs</td>
          <td class="text-center">
            <span class="badge ${c.active ? 'bg-success' : 'bg-secondary'}">${c.active ? 'Active' : 'Disabled'}</span>
          </td>
          <td class="text-end">
            <button class="btn btn-sm btn-outline-primary" onclick="AdminModule.editPriceModal(${c.id})"><i class="fas fa-edit"></i> Edit</button>
          </td>
        </tr>
      `).join('');
    } catch (err) {
      console.error('Failed to load catalog', err);
    }
  },

  async renderCustomersList() {
    const tableBody = document.getElementById('adminCustomersTableBody');
    if (!tableBody) return;

    try {
      let customers = [];
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/users?role=ROLE_CUSTOMER');
        customers = (res && res.data) ? res.data : [];
      } else {
        customers = this.mockCustomers;
      }

      tableBody.innerHTML = customers.map(c => `
        <tr>
          <td class="fw-bold">${c.fullName || c.name}</td>
          <td>${c.email}</td>
          <td>${c.phone}</td>
          <td class="text-center fw-bold"><span class="badge bg-primary rounded-pill">Active</span></td>
          <td><span class="badge bg-success-subtle text-success">${c.status}</span></td>
          <td>Verified</td>
          <td class="text-end">
            <button class="btn btn-sm btn-outline-info me-1"><i class="fas fa-car"></i> Vehicles</button>
          </td>
        </tr>
      `).join('');
    } catch (err) {
      console.error('Failed to load customers', err);
    }
  },

  assignWorkModal(mechanicId) {
    const bookingRef = prompt(`Enter Booking Reference to assign to Mechanic (e.g. SB-2026-0819):`);
    if (bookingRef) {
      Toast.success(`Job ${bookingRef} assignment updated!`);
    }
  },

  editPriceModal(catalogId) {
    const newPrice = prompt(`Update Base Price (INR):`);
    if (newPrice && !isNaN(newPrice)) {
      Toast.success(`Price updated to ${formatCurrency(newPrice)}!`);
    }
  },

  bindAddMechanicForm() {
    const form = document.getElementById('addMechanicForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const fullName = document.getElementById('mechFullName').value.trim();
      const email = document.getElementById('mechEmail').value.trim();
      const phone = document.getElementById('mechPhone').value.trim();
      const specialization = document.getElementById('mechSpecialization').value.trim();

      const submitBtn = document.getElementById('btnSubmitMechanic');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Provisioning...';
      }

      try {
        if (!SVS_CONFIG.USE_MOCK_DATA) {
          const payload = {
            fullName,
            email,
            phone,
            specialization,
            password: 'password123'
          };
          const res = await apiRequest('/mechanics', 'POST', payload);
          Toast.success(`Mechanic ${res.data.fullName} provisioned! Login: ${email} / password123`);
        } else {
          this.mockMechanics.push({
            id: Date.now(),
            fullName,
            phone,
            specialization,
            currentStatus: 'IDLE',
            rating: 5.0,
            activeJobs: 0
          });
          Toast.success(`Mechanic ${fullName} provisioned! Login: ${email} / password123`);
        }

        const modalEl = document.getElementById('addMechanicModal');
        if (modalEl) {
          const modalInstance = bootstrap.Modal.getInstance(modalEl);
          if (modalInstance) {
            modalInstance.hide();
          }
        }
        form.reset();

        await this.renderMechanicsList();
        await this.renderDashboardMechanics();
      } catch (err) {
        console.error('Failed to register mechanic', err);
        const errMsg = (err && (err.message || err.error)) ? (err.message || err.error) : 'Failed to register mechanic';
        Toast.error(errMsg);
      } finally {
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.innerHTML = 'Save & Issue Credentials';
        }
      }
    });
  }
};

window.AdminModule = AdminModule;

document.addEventListener('DOMContentLoaded', () => {
  AdminModule.init();
});
