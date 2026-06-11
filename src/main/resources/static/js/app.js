const API_BASE = '/api';

// Redirige si ya hay sesión activa (solo en login y registro)
if (window.location.pathname.includes('login') || 
    window.location.pathname.includes('registro')) {
    const usuarioGuardado = sessionStorage.getItem('usuario');
    if (usuarioGuardado) {
        const u = JSON.parse(usuarioGuardado);
        if (u.rol === 'CLIENTE') window.location.href = 'dashboard-cliente.html';
        else if (u.rol === 'CAMARERO') window.location.href = 'dashboard-camarero.html';
        else if (u.rol === 'GERENTE') window.location.href = 'dashboard-gerente.html';
    }
}

async function submitLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;
    const errorEl = document.getElementById('login-error');
    errorEl.textContent = '';

    try {
        const res = await fetch(`${API_BASE}/usuarios/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
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

        if (usuario.rol === 'CLIENTE') window.location.href = 'dashboard-cliente.html';
        else if (usuario.rol === 'CAMARERO') window.location.href = 'dashboard-camarero.html';
        else if (usuario.rol === 'GERENTE') window.location.href = 'dashboard-gerente.html';
        else window.location.href = 'dashboard.html';

    } catch {
        errorEl.textContent = 'No se pudo conectar con el servidor.';
    }
}

function cerrarSesion() {
    sessionStorage.removeItem('usuario');
    window.location.href = 'login.html';
}