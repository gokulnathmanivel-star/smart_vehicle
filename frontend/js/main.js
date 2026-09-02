/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Global Application Core JavaScript (main.js)
 */

const SVS_CONFIG = {
  API_BASE_URL: 'http://localhost:8080/api/v1',
  TOKEN_KEY: 'svs_jwt_token',
  USER_KEY: 'svs_user_profile',
  USE_MOCK_DATA: false // Connected to live Spring Boot backend on port 8080
};

// Global Store & State
const AppState = {
  currentUser: null,
  token: null,

  init() {
    this.token = localStorage.getItem(SVS_CONFIG.TOKEN_KEY) || localStorage.getItem('svsbas_jwt_token');
    const storedUser = localStorage.getItem(SVS_CONFIG.USER_KEY);
    if (storedUser) {
      try {
        this.currentUser = JSON.parse(storedUser);
      } catch (e) {
        console.error('Failed to parse user session', e);
        this.clearSession();
      }
    }
  },

  setSession(token, user) {
    this.token = token;
    this.currentUser = user;
    localStorage.setItem(SVS_CONFIG.TOKEN_KEY, token);
    localStorage.setItem('svsbas_jwt_token', token);
    if (user) {
      localStorage.setItem(SVS_CONFIG.USER_KEY, JSON.stringify(user));
      localStorage.setItem('svsbas_user_role', user.role || '');
    }
  },

  clearSession() {
    this.token = null;
    this.currentUser = null;
    localStorage.removeItem(SVS_CONFIG.TOKEN_KEY);
    localStorage.removeItem('svsbas_jwt_token');
    localStorage.removeItem(SVS_CONFIG.USER_KEY);
    localStorage.removeItem('svsbas_user_role');
  },

  isAuthenticated() {
    return !!this.token;
  },

  hasRole(role) {
    return this.currentUser && this.currentUser.role === role;
  }
};

// Initialize App on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
  AppState.init();
  initSidebarToggle();
  initActiveNavHighlight();
  renderUserBadgeInTopbar();
});

/**
 * Mobile Sidebar Toggle Handler
 */
function initSidebarToggle() {
  const toggler = document.getElementById('sidebarToggler');
  const sidebar = document.getElementById('appSidebar');
  const backdrop = document.getElementById('sidebarBackdrop');

  if (toggler && sidebar) {
    toggler.addEventListener('click', () => {
      sidebar.classList.toggle('show');
      if (backdrop) backdrop.classList.toggle('show');
    });
  }

  if (backdrop && sidebar) {
    backdrop.addEventListener('click', () => {
      sidebar.classList.remove('show');
      backdrop.classList.remove('show');
    });
  }
}

/**
 * Highlights the current active navigation link in sidebar
 */
function initActiveNavHighlight() {
  const currentPath = window.location.pathname.split('/').pop();
  const navLinks = document.querySelectorAll('.sidebar-link');
  navLinks.forEach(link => {
    const href = link.getAttribute('href');
    if (href && href.endsWith(currentPath)) {
      link.classList.add('active');
    }
  });
}

/**
 * Renders user info badge in dashboard navbar / sidebar
 */
function renderUserBadgeInTopbar() {
  const user = AppState.currentUser;
  const userNameEls = document.querySelectorAll('.dynamic-user-name');
  const userRoleEls = document.querySelectorAll('.dynamic-user-role');
  const userAvatarEls = document.querySelectorAll('.dynamic-user-avatar');

  if (user) {
    userNameEls.forEach(el => el.textContent = user.fullName || user.name || 'User');
    userRoleEls.forEach(el => el.textContent = formatRole(user.role));
    userAvatarEls.forEach(el => {
      el.textContent = (user.fullName || 'U').charAt(0).toUpperCase();
    });
  }
}

/**
 * Global Toast Notification Utility
 */
const Toast = {
  show(message, type = 'info', duration = 3500) {
    let container = document.getElementById('toastFloatingContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toastFloatingContainer';
      container.className = 'toast-floating-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `custom-toast ${type}`;

    let iconClass = 'fa-info-circle text-primary';
    if (type === 'success') iconClass = 'fa-check-circle text-success';
    if (type === 'error') iconClass = 'fa-exclamation-triangle text-danger';
    if (type === 'warning') iconClass = 'fa-bell text-warning';

    toast.innerHTML = `
      <i class="fas ${iconClass} fa-lg"></i>
      <div class="flex-grow-1">
        <div style="font-weight: 600; font-size: 0.88rem;">${message}</div>
      </div>
      <button type="button" style="background:none; border:none; color:#94a3b8; cursor:pointer;" onclick="this.parentElement.remove()">
        <i class="fas fa-times"></i>
      </button>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transition = 'opacity 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, duration);
  },

  success(msg) { this.show(msg, 'success'); },
  error(msg) { this.show(msg, 'error', 4500); },
  warning(msg) { this.show(msg, 'warning'); },
  info(msg) { this.show(msg, 'info'); }
};

/**
 * API Fetch Client with Automatic Bearer Token Injection
 */
async function apiRequest(endpoint, method = 'GET', body = null) {
  const url = `${SVS_CONFIG.API_BASE_URL}${endpoint}`;
  const headers = {
    'Content-Type': 'application/json'
  };

  if (AppState.token) {
    headers['Authorization'] = `Bearer ${AppState.token}`;
  }

  const options = { method, headers };
  if (body) {
    options.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(url, options);
    const data = await response.json();

    if (!response.ok) {
      if (response.status === 401) {
        AppState.clearSession();
        const p = window.location.pathname.replace(/\\/g, '/');
        const inSub = p.includes('/customer/') || p.includes('/admin/') || p.includes('/mechanic/');
        window.location.href = inSub ? '../login.html?expired=true' : 'login.html?expired=true';
      }
      throw new Error(data.message || 'API request failed');
    }
    return data;
  } catch (error) {
    console.error(`API Error [${method} ${endpoint}]:`, error);
    throw error;
  }
}

/**
 * Format Helpers
 */
function formatCurrency(amount) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2
  }).format(amount || 0);
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  const d = new Date(dateStr);
  return d.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

function formatRole(role) {
  if (!role) return 'Guest';
  switch (role) {
    case 'ROLE_ADMIN': return 'Administrator';
    case 'ROLE_MECHANIC': return 'Certified Mechanic';
    case 'ROLE_CUSTOMER': return 'Vehicle Owner';
    default: return role.replace('ROLE_', '');
  }
}

function getStatusBadge(status) {
  const map = {
    REQUESTED: '<span class="status-badge badge-requested">Requested</span>',
    ASSIGNED: '<span class="status-badge badge-assigned">Assigned</span>',
    IN_PROGRESS: '<span class="status-badge badge-in-progress">In Progress</span>',
    INVOICED: '<span class="status-badge badge-invoiced">Invoiced</span>',
    COMPLETED: '<span class="status-badge badge-completed">Completed</span>',
    CANCELLED: '<span class="status-badge badge-cancelled">Cancelled</span>',
    DISPATCHED: '<span class="status-badge badge-sos-dispatched">SOS Dispatched</span>',
    MECHANIC_EN_ROUTE: '<span class="status-badge badge-mechanic-en-route">En Route</span>',
    ON_SITE: '<span class="status-badge badge-on-site">On Site</span>',
    RESOLVED: '<span class="status-badge badge-resolved">Resolved</span>',
    TOW_REQUIRED: '<span class="status-badge badge-tow-required">Tow Required</span>'
  };
  return map[status] || `<span class="status-badge">${status}</span>`;
}
