/**
 * Smart Vehicle Service and Breakdown Assistance System (SVS-BAS)
 * Production-Ready Vanilla JS API Bridge (api.js)
 * Establishes Link 1: Browser (:5500) <== HTTP / JWT ==> Spring Boot (:8080)
 */

// 1. Define base URL
const API_BASE = 'http://localhost:8080/api/v1';

// 2. Storage keys for JWT lifecycle management
const STORAGE_KEYS = {
  TOKEN: 'svsbas_jwt_token',
  ROLE: 'svsbas_user_role'
};

/**
 * Token Lifecycle Helpers
 */
function getAuthToken() {
  return localStorage.getItem(STORAGE_KEYS.TOKEN);
}

function getAuthRole() {
  return localStorage.getItem(STORAGE_KEYS.ROLE);
}

function setAuthSession(token, role) {
  if (token) localStorage.setItem(STORAGE_KEYS.TOKEN, token);
  if (role) localStorage.setItem(STORAGE_KEYS.ROLE, role);
}

function clearAuthSession() {
  localStorage.removeItem(STORAGE_KEYS.TOKEN);
  localStorage.removeItem(STORAGE_KEYS.ROLE);
}

/**
 * 3. apiFetch wrapper around standard window.fetch()
 *
 * @param {string} endpoint - API path relative to API_BASE (e.g., '/auth/login' or '/vehicles')
 * @param {RequestInit} [options={}] - Standard Fetch options (method, body, headers, etc.)
 * @returns {Promise<any>} - Resolves with parsed JSON payload on HTTP 2xx, rejects otherwise
 */
async function apiFetch(endpoint, options = {}) {
  // Ensure leading slash
  const cleanEndpoint = endpoint.startsWith('/') ? endpoint : '/' + endpoint;
  const url = `${API_BASE}${cleanEndpoint}`;

  // Clone headers or initialize new Headers
  const headers = new Headers(options.headers || {});

  // Set default JSON headers if not uploading FormData/blobs
  if (!(options.body instanceof FormData)) {
    if (!headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }
  }
  if (!headers.has('Accept')) {
    headers.set('Accept', 'application/json');
  }

  // Automatically inject Bearer JWT if present
  const token = getAuthToken();
  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const fetchConfig = {
    ...options,
    headers
  };

  try {
    const response = await window.fetch(url, fetchConfig);

    // 401 Unauthorized handling: clear storage and redirect
    if (response.status === 401) {
      clearAuthSession();
      // Calculate relative path to login.html depending on current folder depth
      const isSubDir = window.location.pathname.includes('/customer/') ||
                        window.location.pathname.includes('/admin/') ||
                        window.location.pathname.includes('/mechanic/');
      const loginUrl = isSubDir ? '../login.html' : 'login.html';
      
      // Prevent infinite redirect loop if already on login page
      if (!window.location.pathname.endsWith('login.html')) {
        window.location.href = loginUrl;
      }
      
      throw new Error('Session expired. Please log in again.');
    }

    // Attempt to parse JSON response body
    let responseData = null;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      responseData = await response.json();
    } else {
      const text = await response.text();
      responseData = { message: text };
    }

    // Check HTTP status code
    if (!response.ok) {
      const errorMessage = (responseData && (responseData.message || responseData.error))
        ? (responseData.message || responseData.error)
        : `Request failed with HTTP status ${response.status}`;
      return Promise.reject(responseData || { error: errorMessage, status: response.status });
    }

    return responseData;
  } catch (networkError) {
    console.error(`[apiFetch Error] ${options.method || 'GET'} ${url}:`, networkError);
    throw networkError;
  }
}

// Global browser window attachment for non-modular script tags
if (typeof window !== 'undefined') {
  window.API_BASE = API_BASE;
  window.apiFetch = apiFetch;
  window.SvsApi = {
    API_BASE,
    apiFetch,
    getAuthToken,
    getAuthRole,
    setAuthSession,
    clearAuthSession
  };
}

// ES module export support
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { API_BASE, apiFetch, getAuthToken, getAuthRole, setAuthSession, clearAuthSession };
}
