/**
 * Turf-Ballers API Client
 * Centralized API communication layer with JWT auth management,
 * error handling, and seamless local mock fallback when backend is offline.
 */

// ---- Configuration ----
const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  ? 'http://localhost:8080/api'
  : 'https://turfballers-api-v2.onrender.com/api';

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
    window.location.href = 'login.html';
  },

  /** Redirect to login if not authenticated */
  requireAuth: () => {
    if (!Auth.isAuthenticated()) {
      window.location.href = 'login.html';
      return false;
    }
    return true;
  }
};

// ---- Mock Local Database for Offline / Demo Mode ----
const MockStore = {
  KEY: 'tb_mock_db_v1',

  getDB() {
    let data = localStorage.getItem(this.KEY);
    if (!data) {
      const today = new Date().toISOString().split('T')[0];
      const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0];
      const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
      const threeDaysAgo = new Date(Date.now() - 259200000).toISOString().split('T')[0];

      const initialDB = {
        users: [
          { email: 'admin@turfballers.com', password: 'Admin@123', fullName: 'Turf Admin', role: 'ADMIN', phone: '+91-9876543210' }
        ],
        members: [
          { id: 1, fullName: 'Arjun Sharma', email: 'arjun@example.com', phone: '+91-9876500001', membershipPlan: 'VIP', status: 'ACTIVE', joinDate: '2024-04-15', notes: 'Regular player' },
          { id: 2, fullName: 'Priya Patel', email: 'priya@example.com', phone: '+91-9876500002', membershipPlan: 'STANDARD', status: 'ACTIVE', joinDate: '2024-06-10', notes: 'Corporate tournaments' },
          { id: 3, fullName: 'Rahul Singh', email: 'rahul@example.com', phone: '+91-9876500003', membershipPlan: 'BASIC', status: 'ACTIVE', joinDate: '2024-07-20', notes: 'Evening slot preference' },
          { id: 4, fullName: 'Sneha Reddy', email: 'sneha@example.com', phone: '+91-9876500004', membershipPlan: 'STANDARD', status: 'INACTIVE', joinDate: '2024-03-05', notes: 'On leave' },
          { id: 5, fullName: 'Vikram Nair', email: 'vikram@example.com', phone: '+91-9876500005', membershipPlan: 'VIP', status: 'ACTIVE', joinDate: '2024-08-12', notes: 'Pickleball enthusiast' }
        ],
        bookings: [
          { id: 1, memberId: 1, memberName: 'Arjun Sharma', turfName: 'Football 7v7', bookingDate: today, startTime: '08:00', endTime: '10:00', amount: 1200, status: 'CONFIRMED', notes: 'Morning practice', createdAt: new Date().toISOString() },
          { id: 2, memberId: 2, memberName: 'Priya Patel', turfName: 'Cricket Box', bookingDate: today, startTime: '10:00', endTime: '12:00', amount: 800, status: 'CONFIRMED', notes: 'Friendly tournament', createdAt: new Date().toISOString() },
          { id: 3, memberId: 3, memberName: 'Rahul Singh', turfName: 'Football 5v5', bookingDate: tomorrow, startTime: '16:00', endTime: '18:00', amount: 600, status: 'PENDING', notes: '', createdAt: new Date().toISOString() },
          { id: 4, memberId: 5, memberName: 'Vikram Nair', turfName: 'Pickleball Court', bookingDate: yesterday, startTime: '07:00', endTime: '08:00', amount: 400, status: 'CONFIRMED', notes: 'Single court', createdAt: new Date().toISOString() },
          { id: 5, memberId: 1, memberName: 'Arjun Sharma', turfName: 'Football 7v7', bookingDate: threeDaysAgo, startTime: '18:00', endTime: '20:00', amount: 1200, status: 'CONFIRMED', notes: 'Weekend league', createdAt: new Date().toISOString() }
        ],
        payments: [
          { id: 1, memberId: 1, memberName: 'Arjun Sharma', bookingId: 1, amount: 1200, paymentMethod: 'UPI', status: 'PAID', transactionRef: 'UPI-2024-001', paymentDate: today, createdAt: new Date().toISOString() },
          { id: 2, memberId: 2, memberName: 'Priya Patel', bookingId: 2, amount: 800, paymentMethod: 'CASH', status: 'PAID', transactionRef: 'CASH-REC-02', paymentDate: today, createdAt: new Date().toISOString() },
          { id: 3, memberId: 3, memberName: 'Rahul Singh', bookingId: 3, amount: 600, paymentMethod: 'CARD', status: 'PENDING', transactionRef: 'CARD-AUTH-99', paymentDate: today, createdAt: new Date().toISOString() },
          { id: 4, memberId: 5, memberName: 'Vikram Nair', bookingId: 4, amount: 400, paymentMethod: 'UPI', status: 'PAID', transactionRef: 'UPI-2024-002', paymentDate: yesterday, createdAt: new Date().toISOString() }
        ],
        attendance: [
          { id: 1, memberId: 1, memberName: 'Arjun Sharma', bookingId: 1, date: today, status: 'PRESENT', checkInTime: '08:05', notes: '' },
          { id: 2, memberId: 2, memberName: 'Priya Patel', bookingId: 2, date: today, status: 'PRESENT', checkInTime: '10:00', notes: '' },
          { id: 3, memberId: 5, memberName: 'Vikram Nair', bookingId: 4, date: yesterday, status: 'PRESENT', checkInTime: '07:02', notes: '' },
          { id: 4, memberId: 4, memberName: 'Sneha Reddy', bookingId: null, date: yesterday, status: 'ABSENT', checkInTime: null, notes: 'Notified' }
        ]
      };
      this.saveDB(initialDB);
      return initialDB;
    }
    return JSON.parse(data);
  },

  saveDB(db) {
    localStorage.setItem(this.KEY, JSON.stringify(db));
  },

  handleMockRequest(endpoint, options = {}) {
    const db = this.getDB();
    const method = (options.method || 'GET').toUpperCase();
    let body = options.body;
    if (typeof body === 'string') {
      try { body = JSON.parse(body); } catch (e) {}
    }

    const [pathOnly, queryString] = endpoint.split('?');
    const queryParams = Object.fromEntries(new URLSearchParams(queryString || ''));

    // 1. Auth: /auth/login
    if (pathOnly === '/auth/login' && method === 'POST') {
      const email = body?.email || '';
      const password = body?.password || '';
      const user = db.users.find(u => u.email.toLowerCase() === email.toLowerCase());
      if (!user || user.password !== password) {
        throw new ApiError('Invalid email or password', 401);
      }
      return {
        token: 'jwt-token-' + Date.now(),
        email: user.email,
        fullName: user.fullName,
        role: user.role || 'ADMIN',
        avatarUrl: user.avatarUrl || null,
        message: 'Login successful'
      };
    }

    // Auth: /auth/register
    if (pathOnly === '/auth/register' && method === 'POST') {
      const email = body?.email || '';
      const password = body?.password || '';
      const fullName = body?.fullName || 'Administrator';
      if (!email || !password) {
        throw new ApiError('Email and password are required', 400);
      }
      const existing = db.users.find(u => u.email.toLowerCase() === email.toLowerCase());
      if (existing) {
        throw new ApiError('Email already registered: ' + email, 400);
      }
      const newUser = { email, password, fullName, role: 'ADMIN', phone: '', avatarUrl: null };
      db.users.push(newUser);
      this.saveDB(db);
      return {
        token: 'jwt-token-' + Date.now(),
        email: newUser.email,
        fullName: newUser.fullName,
        role: newUser.role,
        avatarUrl: null,
        message: 'Registration successful'
      };
    }

    // Auth profile
    if (pathOnly === '/auth/profile') {
      const currentUser = Auth.getUser();
      const user = db.users.find(u => u.email.toLowerCase() === (currentUser?.email || '').toLowerCase());
      return {
        email: user?.email || currentUser?.email || '',
        fullName: user?.fullName || currentUser?.fullName || 'Admin',
        role: user?.role || 'ADMIN',
        phone: user?.phone || ''
      };
    }

    // 2. Dashboard: /dashboard/stats
    if (pathOnly === '/dashboard/stats') {
      const today = new Date().toISOString().split('T')[0];
      const totalMembers = db.members.length;
      const activeMembers = db.members.filter(m => m.status === 'ACTIVE').length;
      const totalBookings = db.bookings.length;
      const todayBookings = db.bookings.filter(b => b.bookingDate === today).length;
      const pendingBookings = db.bookings.filter(b => b.status === 'PENDING').length;
      const monthlyRevenue = db.bookings
        .filter(b => b.status === 'CONFIRMED')
        .reduce((sum, b) => sum + Number(b.amount || 0), 0);
      const pendingPayments = db.payments.filter(p => p.status === 'PENDING').length;

      // Attendance rate today
      const todayAtt = db.attendance.filter(a => a.date === today);
      const presentCount = todayAtt.filter(a => a.status === 'PRESENT').length;
      const attendanceRate = todayAtt.length > 0 ? Math.round((presentCount / todayAtt.length) * 100) : 85;

      // Revenue trend for last 7 days
      const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
      const revenueTrend = [];
      for (let i = 6; i >= 0; i--) {
        const d = new Date(Date.now() - i * 86400000);
        const dStr = d.toISOString().split('T')[0];
        const dayName = days[d.getDay()];
        const dayRev = db.bookings
          .filter(b => b.bookingDate === dStr && b.status === 'CONFIRMED')
          .reduce((sum, b) => sum + Number(b.amount || 0), 0);
        revenueTrend.push({ date: dayName, amount: dayRev || (i === 0 ? 2000 : (i === 1 ? 400 : (i === 3 ? 1200 : 800))) });
      }

      // Membership breakdown
      const membershipBreakdown = { BASIC: 0, STANDARD: 0, VIP: 0 };
      db.members.forEach(m => {
        const p = m.membershipPlan || 'BASIC';
        membershipBreakdown[p] = (membershipBreakdown[p] || 0) + 1;
      });

      return {
        totalMembers,
        activeMembers,
        totalBookings,
        todayBookings,
        pendingBookings,
        monthlyRevenue,
        pendingPayments,
        attendanceRate,
        revenueTrend,
        membershipBreakdown
      };
    }

    // 3. Members
    if (pathOnly === '/members') {
      if (method === 'GET') {
        let list = [...db.members];
        if (queryParams.search) {
          const s = queryParams.search.toLowerCase();
          list = list.filter(m => m.fullName.toLowerCase().includes(s) || m.email.toLowerCase().includes(s) || (m.phone && m.phone.includes(s)));
        }
        if (queryParams.status) {
          list = list.filter(m => m.status === queryParams.status);
        }
        if (queryParams.plan) {
          list = list.filter(m => m.membershipPlan === queryParams.plan);
        }
        const page = parseInt(queryParams.page || '0', 10);
        const size = parseInt(queryParams.size || '10', 10);
        const start = page * size;
        const paged = list.slice(start, start + size);
        return {
          content: paged,
          totalElements: list.length,
          totalPages: Math.max(1, Math.ceil(list.length / size)),
          number: page
        };
      }
      if (method === 'POST') {
        const newMember = {
          id: db.members.length ? Math.max(...db.members.map(m => m.id)) + 1 : 1,
          fullName: body.fullName,
          email: body.email,
          phone: body.phone || '',
          membershipPlan: body.membershipPlan || 'BASIC',
          status: body.status || 'ACTIVE',
          joinDate: body.joinDate || new Date().toISOString().split('T')[0],
          notes: body.notes || ''
        };
        db.members.push(newMember);
        this.saveDB(db);
        return newMember;
      }
    }

    const memberIdMatch = pathOnly.match(/^\/members\/(\d+)$/);
    if (memberIdMatch) {
      const id = parseInt(memberIdMatch[1], 10);
      const idx = db.members.findIndex(m => m.id === id);
      if (idx === -1) throw new ApiError('Member not found', 404);

      if (method === 'GET') return db.members[idx];
      if (method === 'PUT') {
        db.members[idx] = { ...db.members[idx], ...body, id };
        this.saveDB(db);
        return db.members[idx];
      }
      if (method === 'DELETE') {
        db.members.splice(idx, 1);
        this.saveDB(db);
        return { message: 'Member deleted' };
      }
    }

    // 4. Bookings
    if (pathOnly === '/bookings') {
      if (method === 'GET') {
        let list = [...db.bookings];
        if (queryParams.search) {
          const s = queryParams.search.toLowerCase();
          list = list.filter(b => (b.memberName && b.memberName.toLowerCase().includes(s)) || (b.turfName && b.turfName.toLowerCase().includes(s)));
        }
        if (queryParams.date) {
          list = list.filter(b => b.bookingDate === queryParams.date);
        }
        if (queryParams.status) {
          list = list.filter(b => b.status === queryParams.status);
        }
        if (queryParams.memberId) {
          list = list.filter(b => String(b.memberId) === String(queryParams.memberId));
        }

        // Sort desc by date
        list.sort((a, b) => (b.bookingDate > a.bookingDate ? 1 : -1));

        const page = parseInt(queryParams.page || '0', 10);
        const size = parseInt(queryParams.size || '10', 10);
        const start = page * size;
        const paged = list.slice(start, start + size);
        return {
          content: paged,
          totalElements: list.length,
          totalPages: Math.max(1, Math.ceil(list.length / size)),
          number: page
        };
      }

      if (method === 'POST') {
        const member = db.members.find(m => m.id === parseInt(body.memberId, 10));
        const newBooking = {
          id: db.bookings.length ? Math.max(...db.bookings.map(b => b.id)) + 1 : 1,
          memberId: parseInt(body.memberId, 10),
          memberName: member ? member.fullName : (body.memberName || 'Member #' + body.memberId),
          turfName: body.turfName,
          bookingDate: body.bookingDate,
          startTime: body.startTime,
          endTime: body.endTime,
          amount: Number(body.amount) || 0,
          status: body.status || 'CONFIRMED',
          notes: body.notes || '',
          createdAt: new Date().toISOString()
        };

        // Double booking check: same turf, same date, overlapping time, not cancelled
        const overlap = db.bookings.some(b =>
          b.status !== 'CANCELLED' &&
          b.turfName === newBooking.turfName &&
          b.bookingDate === newBooking.bookingDate &&
          b.startTime < newBooking.endTime &&
          b.endTime > newBooking.startTime
        );

        if (overlap) {
          throw new ApiError('Slot already booked for this turf and time range!', 400);
        }

        db.bookings.unshift(newBooking);
        this.saveDB(db);
        return newBooking;
      }
    }

    const bookingCancelMatch = pathOnly.match(/^\/bookings\/(\d+)\/cancel$/);
    if (bookingCancelMatch && method === 'PATCH') {
      const id = parseInt(bookingCancelMatch[1], 10);
      const booking = db.bookings.find(b => b.id === id);
      if (!booking) throw new ApiError('Booking not found', 404);
      booking.status = 'CANCELLED';
      this.saveDB(db);
      return booking;
    }

    const bookingIdMatch = pathOnly.match(/^\/bookings\/(\d+)$/);
    if (bookingIdMatch) {
      const id = parseInt(bookingIdMatch[1], 10);
      const idx = db.bookings.findIndex(b => b.id === id);
      if (idx === -1) throw new ApiError('Booking not found', 404);
      if (method === 'GET') return db.bookings[idx];
      if (method === 'PUT') {
        db.bookings[idx] = { ...db.bookings[idx], ...body, id };
        this.saveDB(db);
        return db.bookings[idx];
      }
      if (method === 'DELETE') {
        db.bookings.splice(idx, 1);
        this.saveDB(db);
        return { message: 'Booking deleted' };
      }
    }

    // 5. Payments
    if (pathOnly === '/payments') {
      if (method === 'GET') {
        return { content: db.payments, totalElements: db.payments.length };
      }
      if (method === 'POST') {
        const member = db.members.find(m => m.id === parseInt(body.memberId, 10));
        const newPayment = {
          id: db.payments.length ? Math.max(...db.payments.map(p => p.id)) + 1 : 1,
          memberId: parseInt(body.memberId, 10),
          memberName: member ? member.fullName : 'Member',
          bookingId: body.bookingId ? parseInt(body.bookingId, 10) : null,
          amount: Number(body.amount) || 0,
          paymentMethod: body.paymentMethod || 'UPI',
          status: body.status || 'PAID',
          transactionRef: body.transactionRef || 'TXN-' + Math.floor(100000 + Math.random() * 900000),
          paymentDate: new Date().toISOString().split('T')[0],
          createdAt: new Date().toISOString()
        };
        db.payments.unshift(newPayment);
        this.saveDB(db);
        return newPayment;
      }
    }

    if (pathOnly.startsWith('/payments/member/')) {
      const memberId = pathOnly.split('/').pop();
      return db.payments.filter(p => String(p.memberId) === String(memberId));
    }

    if (pathOnly === '/payments/pending') {
      return db.payments.filter(p => p.status === 'PENDING');
    }

    // 6. Attendance
    if (pathOnly === '/attendance') {
      if (method === 'GET') {
        let list = [...db.attendance];
        if (queryParams.date) {
          list = list.filter(a => a.date === queryParams.date);
        }
        return { content: list, totalElements: list.length };
      }
      if (method === 'POST') {
        const member = db.members.find(m => m.id === parseInt(body.memberId, 10));
        const newAtt = {
          id: db.attendance.length ? Math.max(...db.attendance.map(a => a.id)) + 1 : 1,
          memberId: parseInt(body.memberId, 10),
          memberName: member ? member.fullName : 'Member',
          bookingId: body.bookingId ? parseInt(body.bookingId, 10) : null,
          date: body.date || new Date().toISOString().split('T')[0],
          status: body.status || 'PRESENT',
          checkInTime: new Date().toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit' }),
          notes: body.notes || ''
        };
        db.attendance.unshift(newAtt);
        this.saveDB(db);
        return newAtt;
      }
    }

    return { message: 'OK' };
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
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 2000); // 2s timeout for offline fallback

    const response = await fetch(url, { ...config, signal: controller.signal });
    clearTimeout(timeoutId);

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
    // Backend offline / connection refused -> seamlessly handle with mock store
    console.info(`[TurfBallers] Backend unreachable at ${url}. Using local storage fallback.`);
    return MockStore.handleMockRequest(endpoint, options);
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
