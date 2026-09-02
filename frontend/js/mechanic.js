/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Field Mechanic Operations JavaScript (mechanic.js)
 * Connected to Spring Boot REST API
 */

const MechanicModule = {
  mockAssignedJobs: [],

  async init() {
    await this.renderAssignedJobs();
    this.bindUpdateJobForm();
  },

  async renderAssignedJobs() {
    const container = document.getElementById('mechanicJobsList');
    if (!container) return;

    try {
      let bookings = [];
      let breakdowns = [];

      if (!SVS_CONFIG.USE_MOCK_DATA) {
        const bRes = await apiRequest('/bookings');
        bookings = (bRes && bRes.data) ? bRes.data : [];

        const brRes = await apiRequest('/breakdowns');
        breakdowns = (brRes && brRes.data) ? brRes.data : [];
      }

      const allJobs = [
        ...breakdowns.map(br => ({
          id: br.id,
          type: 'EMERGENCY_SOS',
          ticketRef: br.sosRef,
          customerName: br.customerName,
          customerPhone: br.customerPhone,
          vehicle: `${br.vehicleInfo || 'Vehicle'} [${br.regNumber || ''}]`,
          status: br.status,
          scheduledTime: 'Emergency Rapid Response',
          reportedIssue: `${br.breakdownType} at ${br.locationAddress || 'Current Location'}`
        })),
        ...bookings.map(b => ({
          id: b.id,
          type: 'REGULAR_SERVICE',
          ticketRef: b.bookingRef,
          customerName: b.customerName,
          customerPhone: b.customerPhone,
          vehicle: `${b.vehicleInfo || 'Vehicle'} [${b.regNumber || ''}]`,
          status: b.status,
          scheduledTime: b.preferredSlot ? new Date(b.preferredSlot).toLocaleString('en-IN') : 'Scheduled Slot',
          reportedIssue: (b.services && b.services.length) ? b.services.join(', ') : 'Periodic Maintenance'
        }))
      ];

      if (allJobs.length === 0) {
        container.innerHTML = `
          <div class="col-12 empty-state-box">
            <div class="empty-state-icon"><i class="fas fa-clipboard-check"></i></div>
            <h5 class="empty-state-title">No Active Jobs Assigned</h5>
            <p class="empty-state-desc">You are currently idle. Any new garage bookings or emergency SOS dispatches will appear here.</p>
          </div>
        `;
        return;
      }

      container.innerHTML = allJobs.map(job => `
        <div class="col-lg-6 mb-4">
          <div class="card card-custom p-4 h-100 shadow-sm border">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <span class="badge ${job.type === 'EMERGENCY_SOS' ? 'bg-danger' : 'bg-primary'} font-monospace">
                ${job.type === 'EMERGENCY_SOS' ? '🚨 EMERGENCY SOS' : 'GARAGE BOOKING'}
              </span>
              <span class="status-badge ${job.status === 'COMPLETED' || job.status === 'RESOLVED' ? 'badge-completed' : 'badge-in-progress'}">
                ${job.status}
              </span>
            </div>
            
            <h5 class="fw-bold mb-1 font-monospace text-dark">${job.ticketRef}</h5>
            <div class="text-primary fw-semibold mb-2"><i class="fas fa-car me-1"></i>${job.vehicle}</div>
            
            <p class="small text-secondary mb-3"><i class="fas fa-wrench me-1"></i>${job.reportedIssue}</p>
            
            <div class="bg-light p-2 rounded mb-3 small">
              <div><i class="far fa-user me-1 text-muted"></i><strong>Customer:</strong> ${job.customerName || 'Rahul Sharma'}</div>
              <div><i class="fas fa-phone me-1 text-muted"></i><strong>Contact:</strong> ${job.customerPhone || '+91 98765 43210'}</div>
              <div><i class="far fa-clock me-1 text-muted"></i><strong>Slot:</strong> ${job.scheduledTime}</div>
            </div>

            <div class="mt-auto d-flex justify-content-between align-items-center border-top pt-3">
              <a href="update-job.html?ticket=${job.ticketRef}&id=${job.id}&type=${job.type}" class="btn btn-brand-primary btn-sm">
                <i class="fas fa-edit me-1"></i> Update Progress & Bill
              </a>
            </div>
          </div>
        </div>
      `).join('');
    } catch (err) {
      console.error('Failed to load mechanic jobs', err);
    }
  },

  bindUpdateJobForm() {
    const form = document.getElementById('updateJobForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const status = document.getElementById('jobStatusSelect').value;
      const notes = document.getElementById('workNotes').value;
      const laborHours = parseFloat(document.getElementById('laborHours').value) || 1.0;

      const urlParams = new URLSearchParams(window.location.search);
      const ticketId = urlParams.get('id') || '1';
      const ticketType = urlParams.get('type');

      try {
        if (!SVS_CONFIG.USE_MOCK_DATA) {
          if (ticketType === 'EMERGENCY_SOS') {
            await apiRequest(`/breakdowns/${ticketId}/status`, 'PATCH', { status, resolutionNotes: notes });
          } else {
            await apiRequest(`/bookings/${ticketId}/status`, 'PATCH', { status, mechanicNotes: notes });
            await apiRequest(`/invoices/booking/${ticketId}?laborHours=${laborHours}`, 'POST');
          }
          Toast.success('Job status and billing updated in database!');
        } else {
          Toast.success('Job status and billing updated! (Mock)');
        }

        setTimeout(() => {
          window.location.href = 'assigned-jobs.html';
        }, 800);
      } catch (err) {
        console.error('Failed to update job', err);
        Toast.error(err.message || 'Failed to update job');
      }
    });
  }
};

document.addEventListener('DOMContentLoaded', () => {
  MechanicModule.init();
});
