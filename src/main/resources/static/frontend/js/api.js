// Use same-origin when frontend is served by Spring Boot; otherwise default to local backend.
const API_BASE = window.location.origin.includes('localhost:8080') ? '' : 'http://localhost:8080';

// ── API Helper ──────────────────────────────────────────
const api = {
  getHeaders() {
    const token = localStorage.getItem('accessToken');
    return {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };
  },

  async request(method, endpoint, body = null) {
    try {
      const options = { method, headers: this.getHeaders() };
      if (body) options.body = JSON.stringify(body);
      const res = await fetch(`${API_BASE}${endpoint}`, options);
      const data = await res.json();
      if (res.status === 401) {
        localStorage.clear();
        window.location.href = 'login.html';
        return null;
      }
      return data;
    } catch (e) {
      toast('Network error — is the backend running on port 8080?', 'error');
      return null;
    }
  },

  get:    (ep)       => api.request('GET',    ep),
  post:   (ep, body) => api.request('POST',   ep, body),
  put:    (ep, body) => api.request('PUT',    ep, body),
  delete: (ep)       => api.request('DELETE', ep),
};

// ── Auth Helpers ────────────────────────────────────────
function getUser()  { return JSON.parse(localStorage.getItem('user') || 'null'); }
function getToken() { return localStorage.getItem('accessToken'); }

function requireAuth(allowedRoles = []) {
  const user = getUser();
  if (!user || !getToken()) {
    window.location.href = 'login.html';
    return null;
  }
  if (allowedRoles.length && !allowedRoles.includes(user.role)) {
    window.location.href = 'login.html';
    return null;
  }
  return user;
}

function logout() {
  const user = getUser();
  if (user) api.post('/api/auth/logout').finally(() => {
    localStorage.clear();
    window.location.href = 'login.html';
  });
  else { localStorage.clear(); window.location.href = 'login.html'; }
}

// ── Toast ───────────────────────────────────────────────
function toast(message, type = 'success') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.innerHTML = `
    <span class="toast-icon">${type === 'success' ? '✓' : '✕'}</span>
    <span>${message}</span>`;
  container.appendChild(el);
  setTimeout(() => { el.style.opacity = '0'; el.style.transition = 'opacity 0.3s';
    setTimeout(() => el.remove(), 300); }, 3500);
}

// ── Sidebar user info ───────────────────────────────────
function renderSidebarUser() {
  const user = getUser();
  if (!user) return;
  const nameEl  = document.getElementById('sidebar-name');
  const roleEl  = document.getElementById('sidebar-role');
  const avatarEl = document.getElementById('sidebar-avatar');
  if (nameEl)   nameEl.textContent  = user.name;
  if (roleEl)   roleEl.textContent  = user.role;
  if (avatarEl) avatarEl.textContent = user.name?.slice(0,2).toUpperCase();
}

// ── Format date ─────────────────────────────────────────
function fmtDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('en-IN', { day:'2-digit', month:'short', year:'numeric' });
}

// ── Status badge ─────────────────────────────────────────
function statusBadge(status) {
  const map = { PENDING: 'badge-pending', APPROVED: 'badge-approved', REJECTED: 'badge-rejected' };
  return `<span class="badge ${map[status] || ''}">${status}</span>`;
}

// ── Role badge ───────────────────────────────────────────
function roleBadge(role) {
  const map = { ADMIN: 'badge-admin', MANAGER: 'badge-manager', USER: 'badge-user' };
  return `<span class="badge ${map[role] || ''}">${role}</span>`;
}

// ── Tabs ─────────────────────────────────────────────────
function initTabs() {
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const target = btn.dataset.tab;
      document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
      document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
      btn.classList.add('active');
      document.getElementById(target)?.classList.add('active');
    });
  });
}

// ── Modal ─────────────────────────────────────────────────
function openModal(id)  { document.getElementById(id)?.classList.add('open'); }
function closeModal(id) { document.getElementById(id)?.classList.remove('open'); }
