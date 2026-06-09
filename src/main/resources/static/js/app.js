const API_BASE = '/api';

// Redirige si ya hay sesión activa
const usuarioGuardado = sessionStorage.getItem('usuario');
if (usuarioGuardado && window.location.pathname.includes('login')) {
    window.location.href = 'dashboard.html';
}

async function submitLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;
    const errorEl = document.getElementById('login-error');
    errorEl.textContent = '';

    try {
        const res = await fetch(`${API_BASE}/usuarios/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
            method: 'POST'
        });
        if (res.status === 400 || res.status === 404) {
            errorEl.textContent = 'Credenciales incorrectas. Inténtalo de nuevo.';
            return;
        }
        if (!res.ok) {
            errorEl.textContent = 'Error del servidor. Inténtalo más tarde.';
            return;
        }
        const usuario = await res.json();
        sessionStorage.setItem('usuario', JSON.stringify(usuario));
        window.location.href = 'dashboard.html';
    } catch {
        errorEl.textContent = 'No se pudo conectar con el servidor.';
    }
}

function cerrarSesion() {
    sessionStorage.removeItem('usuario');
    window.location.href = 'login.html';
}