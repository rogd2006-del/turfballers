/**
 * Turf-Ballers API Client
 * Centralized API communication layer with JWT auth management,
 * error handling, and toast notifications.
 */

// ---- Configuration ----
const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  ? 'http://localhost:8080/api'
  : 'https://turfballers-api.onrender.com/api'; // Update with your Render URL

// ---- Token Management ----
const Auth = {
  getToken: () => localStorage.getItem('tb_token'),
  setToken: (token) => localStorage.setItem('tb_token', token),
  removeToken: () => localStorage.removeItem('tb_token'),

  getUser: () => {
    const u = localStorage.getItem('tb_user');
    return u ? JSON.parse(u) : null;
  },
  setUser: (user) => localStorage.setItem('tb_user', JSON.stringify(user)),
  removeUser: () => localStorage.removeItem('tb_user'),

  isAuthenticated: () => !!localStorage.getItem('tb_token'),

  logout: () => {
    Auth.removeToken();
    Auth.removeUser();
    window.location.href = '/login.html';
  },

  /** Redirect to login if not authenticated */
  requireAuth: () => {
    if (!Auth.isAuthenticated()) {
      window.location.href = '/login.html';
      return false;
    }
    return true;
  }
};

// ---- Core HTTP Client ----
async function apiRequest(endpoint, options = {}) {
  const url = `${API_BASE_URL}${endpoint}`;
  const token = Auth.getToken();

  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
      ...options.headers
    },
    ...options
  };

  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body);
  }

  try {
    const response = await fetch(url, config);

    if (response.status === 401) {
      Auth.logout();
      return;
    }

    const contentType = response.headers.get('content-type');
    const data = contentType && contentType.includes('application/json')
      ? await response.json()
      : await response.text();

    if (!response.ok) {
      const message = data?.message || data?.error || `Request failed (${response.status})`;
      throw new ApiError(message, response.status, data?.details);
    }

    return data;
  } catch (err) {
    if (err instanceof ApiError) throw err;
    throw new ApiError('Network error. Please check your connection.', 0);
  }
}

class ApiError extends Error {
  constructor(message, status, details = null) {
    super(message);
    this.status = status;
    this.details = details;
  }
}

// ---- API Methods ----
const API = {
  // Auth
  auth: {
    login: (data) => apiRequest('/auth/login', { method: 'POST', body: data }),
    register: (data) => apiRequest('/auth/register', { method: 'POST', body: data }),
    getProfile: () => apiRequest('/auth/profile'),
    updateProfile: (data) => apiRequest('/auth/profile', { method: 'PUT', body: data }),
    changePassword: (data) => apiRequest('/auth/change-password', { method: 'POST', body: data }),
  },

  // Members
  members: {
    getAll: (params = {}) => {
      const q = new URLSearchParams(params).toString();
      return apiRequest(`/members?${q}`);
    },
    getById: (id) => apiRequest(`/members/${id}`),
    create: (data) => apiRequest('/members', { method: 'POST', body: data }),
    update: (id, data) => apiRequest(`/members/${id}`, { method: 'PUT', body: data }),
    delete: (id) => apiRequest(`/members/${id}`, { method: 'DELETE' }),
  },

  // Bookings
  bookings: {
    getAll: (params = {}) => {
      const q = new URLSearchParams(params).toString();
      return apiRequest(`/bookings?${q}`);
    },
    getCalendar: (start, end) => apiRequest(`/bookings/calendar?start=${start}&end=${end}`),
    getById: (id) => apiRequest(`/bookings/${id}`),
    create: (data) => apiRequest('/bookings', { method: 'POST', body: data }),
    update: (id, data) => apiRequest(`/bookings/${id}`, { method: 'PUT', body: data }),
    cancel: (id) => apiRequest(`/bookings/${id}/cancel`, { method: 'PATCH' }),
    delete: (id) => apiRequest(`/bookings/${id}`, { method: 'DELETE' }),
  },

  // Payments
  payments: {
    getAll: () => apiRequest('/payments'),
    getPending: () => apiRequest('/payments/pending'),
    getByMember: (memberId) => apiRequest(`/payments/member/${memberId}`),
    create: (data) => apiRequest('/payments', { method: 'POST', body: data }),
  },

  // Attendance
  attendance: {
    getAll: (params = {}) => {
      const q = new URLSearchParams(params).toString();
      return apiRequest(`/attendance?${q}`);
    },
    mark: (data) => apiRequest('/attendance', { method: 'POST', body: data }),
    getMemberStats: (memberId) => apiRequest(`/attendance/stats/${memberId}`),
  },

  // Dashboard
  dashboard: {
    getStats: () => apiRequest('/dashboard/stats'),
  }
};

// ---- Toast Notifications ----
const Toast = {
  container: null,

  init() {
    if (!this.container) {
      this.container = document.createElement('div');
      this.container.className = 'toast-container';
      document.body.appendChild(this.container);
    }
  },

  show(message, type = 'info', duration = 3000) {
    this.init();
    const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${icons[type] || ''}</span><span>${message}</span>`;
    this.container.appendChild(toast);
    setTimeout(() => toast.remove(), duration + 400);
  },

  success: (msg) => Toast.show(msg, 'success'),
  error: (msg) => Toast.show(msg, 'error', 4000),
  warning: (msg) => Toast.show(msg, 'warning'),
  info: (msg) => Toast.show(msg, 'info'),
};

// ---- UI Helpers ----
function formatCurrency(amount) {
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(amount || 0);
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatTime(timeStr) {
  if (!timeStr) return '—';
  const [h, m] = timeStr.split(':');
  const d = new Date(); d.setHours(+h, +m);
  return d.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true });
}

function statusBadge(status) {
  const map = {
    ACTIVE: 'badge-success', CONFIRMED: 'badge-success', PAID: 'badge-success', PRESENT: 'badge-success',
    INACTIVE: 'badge-neutral', CANCELLED: 'badge-danger', FAILED: 'badge-danger', ABSENT: 'badge-danger',
    PENDING: 'badge-warning',
    VIP: 'badge-purple', STANDARD: 'badge-info', BASIC: 'badge-neutral',
    CASH: 'badge-warning', UPI: 'badge-info', CARD: 'badge-success', NET_BANKING: 'badge-purple'
  };
  return `<span class="badge ${map[status] || 'badge-neutral'}">${status}</span>`;
}

function planBadge(plan) {
  return statusBadge(plan);
}

function renderPagination(container, page, totalPages, onPage) {
  container.innerHTML = '';
  if (totalPages <= 1) return;

  const prev = document.createElement('button');
  prev.className = 'page-btn';
  prev.textContent = '←';
  prev.disabled = page === 0;
  prev.onclick = () => onPage(page - 1);
  container.appendChild(prev);

  for (let i = 0; i < totalPages; i++) {
    const btn = document.createElement('button');
    btn.className = 'page-btn' + (i === page ? ' active' : '');
    btn.textContent = i + 1;
    btn.onclick = () => onPage(i);
    container.appendChild(btn);
  }

  const next = document.createElement('button');
  next.className = 'page-btn';
  next.textContent = '→';
  next.disabled = page === totalPages - 1;
  next.onclick = () => onPage(page + 1);
  container.appendChild(next);
}

/** Set active nav item */
function setActiveNav() {
  const page = window.location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.nav-item').forEach(item => {
    const href = item.getAttribute('href') || '';
    if (href.includes(page)) item.classList.add('active');
    else item.classList.remove('active');
  });
}

/** Populate user info in sidebar */
function populateSidebarUser() {
  const user = Auth.getUser();
  if (!user) return;
  const nameEl = document.getElementById('sidebarUserName');
  const roleEl = document.getElementById('sidebarUserRole');
  const avatarEl = document.getElementById('sidebarAvatar');
  if (nameEl) nameEl.textContent = user.fullName || 'Admin';
  if (roleEl) roleEl.textContent = user.role || 'Administrator';
  if (avatarEl) avatarEl.textContent = (user.fullName || 'A')[0].toUpperCase();
}

/** Init sidebar (attach logout button, mobile toggle, etc.) */
function initSidebar() {
  populateSidebarUser();
  setActiveNav();
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) logoutBtn.addEventListener('click', Auth.logout);
  const menuToggle = document.getElementById('menuToggle');
  const sidebar = document.querySelector('.sidebar');
  if (menuToggle && sidebar) {
    menuToggle.addEventListener('click', () => sidebar.classList.toggle('open'));
  }
}

// ---- Modal Helpers ----
function openModal(id) {
  document.getElementById(id)?.classList.add('open');
}
function closeModal(id) {
  document.getElementById(id)?.classList.remove('open');
}

// Init Toast
Toast.init();
