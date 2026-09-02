/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Field Mechanic Operations JavaScript (mechanic.js)
 */

const MechanicModule = {
  // Mock Data Store for Assigned Mechanic Work
  mockAssignedJobs: [
    {
      id: 501,
      ticketType: 'REGULAR_SERVICE',
      ticketRef: 'SB-2026-0819',
      customerName: 'Rahul Sharma',
      customerPhone: '+91 98765 43210',
      vehicle: 'Hyundai Creta SX (O) [KA-01-MJ-5021]',
      status: 'ASSIGNED',
      scheduledTime: '2026-09-08 10:30 AM',
      reportedIssue: 'Scheduled 30,000 km periodic service + slight brake squeal',
      workNotes: '',
      laborHours: 0,
      partsUsed: []
    },
    {
      id: 502,
      ticketType: 'EMERGENCY_SOS',
      ticketRef: 'SOS-2026-0902-881',
      customerName: 'Pooja Hegde',
      customerPhone: '+91 91222 33445',
      vehicle: 'Tata Nexon EV Max [KA-05-EV-9912]',
      status: 'MECHANIC_EN_ROUTE',
      scheduledTime: 'Emergency Dispatch (21:15)',
      reportedIssue: 'Flat tyre on Outer Ring Road, Bengaluru',
      workNotes: 'Approaching incident site, ETA 10 mins',
      laborHours: 1.0,
      partsUsed: []
    }
  ],

  init() {
    this.renderAssignedJobs();
    this.bindUpdateJobForm();
  },

  renderAssignedJobs() {
    const container = document.getElementById('mechanicJobsList');
    if (!container) return;

    if (this.mockAssignedJobs.length === 0) {
      container.innerHTML = `
        <div class="col-12 empty-state-box">
          <div class="empty-state-icon"><i class="fas fa-clipboard-check"></i></div>
          <h5 class="empty-state-title">No Active Jobs Assigned</h5>
          <p class="empty-state-desc">You are currently idle. Any new garage bookings or emergency SOS dispatches will appear here.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = this.mockAssignedJobs.map(job => `
      <div class="col-lg-6 mb-4">
        <div class="card-custom p-3 h-100 ${job.ticketType === 'EMERGENCY_SOS' ? 'border-danger border-2' : ''}">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="badge ${job.ticketType === 'EMERGENCY_SOS' ? 'bg-danger' : 'bg-primary'} font-monospace">
              ${job.ticketType === 'EMERGENCY_SOS' ? '🚨 SOS EMERGENCY' : '🔧 WORKSHOP SERVICE'}
            </span>
            ${getStatusBadge(job.status)}
          </div>
          <h5 class="fw-bold mb-1">${job.vehicle}</h5>
          <p class="text-muted small mb-2 font-monospace">Ticket Ref: ${job.ticketRef}</p>

          <div class="bg-light p-2 rounded mb-3 small">
            <div><strong>Customer:</strong> ${job.customerName} (<a href="tel:${job.customerPhone}"><i class="fas fa-phone me-1"></i>${job.customerPhone}</a>)</div>
            <div><strong>Issue / Request:</strong> ${job.reportedIssue}</div>
          </div>

          <div class="d-flex justify-content-between align-items-center mt-auto pt-2 border-top">
            <small class="text-muted"><i class="far fa-clock me-1"></i>${job.scheduledTime}</small>
            <a href="update-job.html?jobId=${job.id}" class="btn btn-sm btn-brand-primary">
              <i class="fas fa-wrench me-1"></i> Manage Job
            </a>
          </div>
        </div>
      </div>
    `).join('');
  },

  bindUpdateJobForm() {
    const form = document.getElementById('mechanicJobUpdateForm');
    if (!form) return;

    // Prepopulate job details from query parameter if available
    const urlParams = new URLSearchParams(window.location.search);
    const jobId = parseInt(urlParams.get('jobId') || '501', 10);
    const currentJob = this.mockAssignedJobs.find(j => j.id === jobId) || this.mockAssignedJobs[0];

    if (currentJob) {
      const refEl = document.getElementById('jobDisplayRef');
      const vehEl = document.getElementById('jobDisplayVehicle');
      const custEl = document.getElementById('jobDisplayCustomer');
      const statusSelect = document.getElementById('jobStatusSelect');

      if (refEl) refEl.textContent = currentJob.ticketRef;
      if (vehEl) vehEl.textContent = currentJob.vehicle;
      if (custEl) custEl.textContent = `${currentJob.customerName} (${currentJob.customerPhone})`;
      if (statusSelect) statusSelect.value = currentJob.status;
    }

    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const newStatus = document.getElementById('jobStatusSelect').value;
      const workNotes = document.getElementById('jobWorkNotes').value;
      const laborHours = parseFloat(document.getElementById('jobLaborHours').value || 0);

      currentJob.status = newStatus;
      currentJob.workNotes = workNotes;
      currentJob.laborHours = laborHours;

      Toast.success(`Job ${currentJob.ticketRef} status updated to ${newStatus}!`);
      setTimeout(() => {
        window.location.href = 'assigned-jobs.html';
      }, 800);
    });
  },

  addPartEntry(partName, unitPrice, qty) {
    Toast.success(`Logged part: ${partName} x ${qty} added to bill.`);
  }
};

document.addEventListener('DOMContentLoaded', () => {
  MechanicModule.init();
});
