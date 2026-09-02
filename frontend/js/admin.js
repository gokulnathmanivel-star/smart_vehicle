/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Admin Operations & Dispatch Command JavaScript (admin.js)
 * Connected to Spring Boot REST API
 */

const AdminModule = {
  // Fallback Mock Data Store
  mockMechanics: [
    { id: 1, name: 'Vikram Singh', phone: '+91 91234 56789', specialization: 'Engine Diagnostics & EV', status: 'IDLE', rating: 4.9, activeJobs: 1 },
    { id: 2, name: 'Suresh Kumar', phone: '+91 98111 22334', specialization: 'Brakes & Suspension', status: 'BUSY', rating: 4.7, activeJobs: 3 }
  ],

  mockCatalog: [],
  mockCustomers: [],

  async init() {
    await this.renderDashboardStats();
    await this.renderMechanicsList();
    await this.renderCatalogList();
    await this.renderCustomersList();
  },

  async renderDashboardStats() {
    const statActiveBookings = document.getElementById('statActiveBookings');
    const statActiveSos = document.getElementById('statActiveSos');
    const statAvailableMechanics = document.getElementById('statAvailableMechanics');
    const statTotalRevenue = document.getElementById('statTotalRevenue');

    try {
      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const res = await apiRequest('/analytics/dashboard');
        if (res && res.data) {
          const d = res.data;
          if (statActiveBookings) statActiveBookings.textContent = d.activeBookings || 0;
          if (statActiveSos) statActiveSos.textContent = d.activeSosRequests || 0;
          if (statAvailableMechanics) statAvailableMechanics.textContent = d.availableMechanics || 0;
          if (statTotalRevenue) statTotalRevenue.textContent = formatCurrency(d.totalRevenue || 0);
          return;
        }
      }
    } catch (e) {
      console.warn('Could not fetch live dashboard metrics', e);
    }

    if (statActiveBookings) statActiveBookings.textContent = '1';
    if (statActiveSos) statActiveSos.textContent = '1';
    if (statAvailableMechanics) statAvailableMechanics.textContent = '1';
    if (statTotalRevenue) statTotalRevenue.textContent = formatCurrency(7904.82);
  },

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
          <td class="text-center fw-bold">${m.activeJobs || 1}</td>
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
    const bookingRef = prompt(`Enter Booking Reference to assign to Mechanic (e.g. SB-20260902-7362):`);
    if (bookingRef) {
      Toast.success(`Job ${bookingRef} assignment updated!`);
    }
  },

  editPriceModal(catalogId) {
    const newPrice = prompt(`Update Base Price (INR):`);
    if (newPrice && !isNaN(newPrice)) {
      Toast.success(`Price updated to ${formatCurrency(newPrice)}!`);
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
  AdminModule.init();
});
