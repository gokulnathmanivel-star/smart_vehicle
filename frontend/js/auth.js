/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Authentication & Session Management JavaScript (auth.js)
 */

const AuthModule = {
  // Pre-configured mock credentials for demonstration & testing
  DEMO_USERS: [
    {
      email: 'customer@demo.com',
      password: 'password123',
      fullName: 'Rahul Sharma',
      phone: '+91 98765 43210',
      role: 'ROLE_CUSTOMER'
    },
    {
      email: 'mechanic@demo.com',
      password: 'password123',
      fullName: 'Vikram Singh (Lead Tech)',
      phone: '+91 91234 56789',
      role: 'ROLE_MECHANIC'
    },
    {
      email: 'admin@demo.com',
      password: 'password123',
      fullName: 'Anita Desai (Operations)',
      phone: '+91 99887 76655',
      role: 'ROLE_ADMIN'
    }
  ],

  init() {
    this.bindLoginForm();
    this.bindRegisterForm();
    this.bindLogoutButtons();
  },

  /**
   * Handles user login submission
   */
  async login(email, password, roleHint = null) {
    if (SVS_CONFIG.USE_MOCK_DATA) {
      // Simulate network latency for realistic UX
      await new Promise(resolve => setTimeout(resolve, 600));

      const matchedUser = this.DEMO_USERS.find(
        u => u.email.toLowerCase() === email.toLowerCase() && u.password === password
      );

      if (!matchedUser) {
        throw new Error('Invalid email or password. Please verify your credentials.');
      }

      const mockToken = 'mock_jwt_' + btoa(JSON.stringify({ sub: matchedUser.email, role: matchedUser.role, exp: Date.now() + 86400000 }));
      AppState.setSession(mockToken, matchedUser);
      return matchedUser;
    } else {
      // Live Spring Boot API Call
      const response = await apiRequest('/auth/login', 'POST', { email, password });
      AppState.setSession(response.data.token, response.data.user);
      return response.data.user;
    }
  },

  /**
   * Handles user registration
   */
  async register(registrationData) {
    if (SVS_CONFIG.USE_MOCK_DATA) {
      await new Promise(resolve => setTimeout(resolve, 800));
      const newUser = {
        fullName: registrationData.fullName,
        email: registrationData.email,
        phone: registrationData.phone,
        password: registrationData.password,
        role: registrationData.role || 'ROLE_CUSTOMER'
      };
      this.DEMO_USERS.push(newUser);
      const mockToken = 'mock_jwt_' + btoa(JSON.stringify({ sub: newUser.email, role: newUser.role }));
      AppState.setSession(mockToken, newUser);
      return newUser;
    } else {
      const response = await apiRequest('/auth/register', 'POST', registrationData);
      return response.data;
    }
  },

  _getPath(target) {
    const p = window.location.pathname.replace(/\\/g, '/');
    const inSub = p.includes('/customer/') || p.includes('/admin/') || p.includes('/mechanic/');
    return inSub ? `../${target}` : target;
  },

  /**
   * Logs out the current user and redirects to login page
   */
  logout() {
    AppState.clearSession();
    Toast.info('Logged out successfully');
    setTimeout(() => {
      window.location.href = this._getPath('login.html');
    }, 400);
  },

  /**
   * Redirects user to their designated role dashboard
   */
  redirectByRole(role) {
    switch (role) {
      case 'ROLE_ADMIN':
        window.location.href = this._getPath('admin/dashboard.html');
        break;
      case 'ROLE_MECHANIC':
        window.location.href = this._getPath('mechanic/dashboard.html');
        break;
      case 'ROLE_CUSTOMER':
      default:
        window.location.href = this._getPath('customer/dashboard.html');
        break;
    }
  },

  /**
   * Enforces role-based route guard on secure pages
   */
  protectRoute(requiredRole) {
    AppState.init();
    if (!AppState.isAuthenticated()) {
      window.location.href = this._getPath('login.html?unauthorized=true');
      return false;
    }
    if (requiredRole && !AppState.hasRole(requiredRole)) {
      alert(`Access Restricted: This page requires ${formatRole(requiredRole)} access privileges.`);
      this.redirectByRole(AppState.currentUser.role);
      return false;
    }
    return true;
  },

  bindLoginForm() {
    const form = document.getElementById('loginForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const email = document.getElementById('loginEmail').value.trim();
      const password = document.getElementById('loginPassword').value;
      const submitBtn = form.querySelector('button[type="submit"]');

      try {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-circle-notch fa-spin me-2"></i>Authenticating...';
        
        const user = await this.login(email, password);
        Toast.success(`Welcome back, ${user.fullName}!`);
        
        setTimeout(() => {
          this.redirectByRole(user.role);
        }, 600);
      } catch (err) {
        Toast.error(err.message);
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i>Sign In';
      }
    });
  },

  bindRegisterForm() {
    const form = document.getElementById('registerForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const fullName = document.getElementById('regFullName').value.trim();
      const email = document.getElementById('regEmail').value.trim();
      const phone = document.getElementById('regPhone').value.trim();
      const password = document.getElementById('regPassword').value;
      const confirmPassword = document.getElementById('regConfirmPassword').value;
      const role = document.getElementById('regRole') ? document.getElementById('regRole').value : 'ROLE_CUSTOMER';

      if (password !== confirmPassword) {
        Toast.error('Passwords do not match. Please re-enter.');
        return;
      }

      const submitBtn = form.querySelector('button[type="submit"]');

      try {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-circle-notch fa-spin me-2"></i>Creating Account...';

        const user = await this.register({ fullName, email, phone, password, role });
        Toast.success('Account created successfully! Redirecting...');

        setTimeout(() => {
          this.redirectByRole(user.role);
        }, 800);
      } catch (err) {
        Toast.error(err.message);
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-user-plus me-2"></i>Create Account';
      }
    });
  },

  bindLogoutButtons() {
    document.querySelectorAll('.btn-logout').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        this.logout();
      });
    });
  }
};

document.addEventListener('DOMContentLoaded', () => {
  AuthModule.init();
});
