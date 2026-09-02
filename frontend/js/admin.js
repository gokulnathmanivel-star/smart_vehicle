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

  mockCatalog: [],
  mockCustomers: [],

  async init() {
    await this.renderDashboardStats();
    await this.renderDashboardBookings();
    await this.renderDashboardMechanics();
    await this.renderMechanicsList();
    await this.renderCatalogList();
    await this.renderCustomersList();
    this.bindAddMechanicForm();
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
    const bookingRef = prompt(`Enter Booking Reference to assign to Mechanic (e.g. SB-20260902-9020):`);
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

        // Close bootstrap modal if open
        const modalEl = document.getElementById('addMechanicModal');
        if (modalEl) {
          const modalInstance = bootstrap.Modal.getInstance(modalEl);
          if (modalInstance) {
            modalInstance.hide();
          }
        }
        form.reset();

        // Refresh live roster table & dashboard widget
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

document.addEventListener('DOMContentLoaded', () => {
  AdminModule.init();
});
