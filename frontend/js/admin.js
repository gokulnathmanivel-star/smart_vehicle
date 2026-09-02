/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Admin Operations & Dispatch Command JavaScript (admin.js)
 */

const AdminModule = {
  // Mock Data Store for Admin Context
  mockMechanics: [
    { id: 1, name: 'Vikram Singh', phone: '+91 91234 56789', specialization: 'Engine Diagnostics & EV', status: 'IDLE', rating: 4.9, activeJobs: 1 },
    { id: 2, name: 'Suresh Kumar', phone: '+91 98111 22334', specialization: 'Brakes & Suspension', status: 'BUSY', rating: 4.7, activeJobs: 3 },
    { id: 3, name: 'Amit Patel', phone: '+91 94555 66778', specialization: 'Electrical & Roadside SOS', status: 'IDLE', rating: 4.8, activeJobs: 0 },
    { id: 4, name: 'Mohammed Farooq', phone: '+91 97777 88990', specialization: 'Transmission & Tyres', status: 'OFFLINE', rating: 4.6, activeJobs: 0 }
  ],

  mockCatalog: [
    { id: 1, code: 'SVC-GEN-01', name: 'Comprehensive Periodic Service', category: 'GENERAL_MAINTENANCE', price: 2999.00, hours: 3.5, active: true },
    { id: 2, code: 'SVC-ENG-02', name: 'Synthetic Engine Oil & Filter Flush', category: 'ENGINE_REPAIR', price: 1850.00, hours: 1.0, active: true },
    { id: 3, code: 'SVC-BRK-03', name: 'Front & Rear Brake Pad Replacement', category: 'TYRES_BRAKES', price: 2400.00, hours: 2.0, active: true },
    { id: 4, code: 'SVC-SOS-04', name: 'Emergency Roadside Dispatch Fee', category: 'EMERGENCY_ROADSIDE', price: 799.00, hours: 1.0, active: true }
  ],

  mockCustomers: [
    { id: 1, name: 'Rahul Sharma', email: 'rahul.s@example.com', phone: '+91 98765 43210', vehiclesCount: 2, status: 'ACTIVE', joined: '2026-01-12' },
    { id: 2, name: 'Pooja Hegde', email: 'pooja.h@example.com', phone: '+91 91222 33445', vehiclesCount: 1, status: 'ACTIVE', joined: '2026-02-04' },
    { id: 3, name: 'Karthik Raja', email: 'karthik.r@example.com', phone: '+91 93444 55667', vehiclesCount: 3, status: 'ACTIVE', joined: '2026-02-28' }
  ],

  init() {
    this.renderDashboardStats();
    this.renderMechanicsList();
    this.renderCatalogList();
    this.renderCustomersList();
  },

  renderDashboardStats() {
    const statActiveBookings = document.getElementById('statActiveBookings');
    const statActiveSos = document.getElementById('statActiveSos');
    const statAvailableMechanics = document.getElementById('statAvailableMechanics');
    const statTotalRevenue = document.getElementById('statTotalRevenue');

    if (statActiveBookings) statActiveBookings.textContent = '14';
    if (statActiveSos) statActiveSos.textContent = '3';
    if (statAvailableMechanics) statAvailableMechanics.textContent = '2';
    if (statTotalRevenue) statTotalRevenue.textContent = formatCurrency(148750);
  },

  renderMechanicsList() {
    const tableBody = document.getElementById('adminMechanicsTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = this.mockMechanics.map(m => `
      <tr>
        <td>
          <div class="fw-bold text-dark">${m.name}</div>
          <small class="text-muted"><i class="fas fa-phone-alt me-1"></i>${m.phone}</small>
        </td>
        <td><span class="badge bg-light text-dark border">${m.specialization}</span></td>
        <td>
          <span class="status-badge ${m.status === 'IDLE' ? 'badge-completed' : m.status === 'BUSY' ? 'badge-in-progress' : 'badge-cancelled'}">
            ${m.status}
          </span>
        </td>
        <td><span class="text-warning fw-bold"><i class="fas fa-star me-1"></i>${m.rating}</span></td>
        <td class="text-center fw-bold">${m.activeJobs}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary me-1" onclick="AdminModule.assignWorkModal(${m.id})">Assign Job</button>
          <button class="btn btn-sm btn-outline-secondary" onclick="AdminModule.toggleMechanicStatus(${m.id})"><i class="fas fa-power-off"></i></button>
        </td>
      </tr>
    `).join('');
  },

  toggleMechanicStatus(id) {
    const mech = this.mockMechanics.find(m => m.id === id);
    if (!mech) return;
    mech.status = mech.status === 'IDLE' ? 'OFFLINE' : 'IDLE';
    Toast.info(`Mechanic ${mech.name} status updated to ${mech.status}`);
    this.renderMechanicsList();
  },

  renderCatalogList() {
    const tableBody = document.getElementById('adminCatalogTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = this.mockCatalog.map(c => `
      <tr>
        <td class="font-monospace fw-bold">${c.code}</td>
        <td class="fw-semibold">${c.name}</td>
        <td><span class="badge bg-secondary-subtle text-dark">${c.category}</span></td>
        <td class="fw-bold text-end">${formatCurrency(c.price)}</td>
        <td class="text-center">${c.hours} hrs</td>
        <td class="text-center">
          <span class="badge ${c.active ? 'bg-success' : 'bg-secondary'}">${c.active ? 'Active' : 'Disabled'}</span>
        </td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary" onclick="AdminModule.editPriceModal(${c.id})"><i class="fas fa-edit"></i> Edit</button>
        </td>
      </tr>
    `).join('');
  },

  renderCustomersList() {
    const tableBody = document.getElementById('adminCustomersTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = this.mockCustomers.map(c => `
      <tr>
        <td class="fw-bold">${c.name}</td>
        <td>${c.email}</td>
        <td>${c.phone}</td>
        <td class="text-center fw-bold"><span class="badge bg-primary rounded-pill">${c.vehiclesCount}</span></td>
        <td><span class="badge bg-success-subtle text-success">${c.status}</span></td>
        <td>${c.joined}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-info me-1"><i class="fas fa-car"></i> Vehicles</button>
        </td>
      </tr>
    `).join('');
  },

  assignWorkModal(mechanicId) {
    const mech = this.mockMechanics.find(m => m.id === mechanicId);
    const bookingRef = prompt(`Assign work to ${mech.name}. Enter Booking Reference (e.g., SB-2026-0819):`);
    if (bookingRef) {
      mech.activeJobs++;
      mech.status = 'BUSY';
      Toast.success(`Job ${bookingRef} successfully assigned to ${mech.name}!`);
      this.renderMechanicsList();
    }
  },

  editPriceModal(catalogId) {
    const item = this.mockCatalog.find(c => c.id === catalogId);
    const newPrice = prompt(`Update Base Price for "${item.name}":`, item.price);
    if (newPrice && !isNaN(newPrice)) {
      item.price = parseFloat(newPrice);
      Toast.success(`Price updated to ${formatCurrency(item.price)}!`);
      this.renderCatalogList();
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
  AdminModule.init();
});
