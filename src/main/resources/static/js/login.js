// Redirige si ya hay sesión activa
const usuarioGuardado = sessionStorage.getItem('usuario');
if (usuarioGuardado) {
    redirigirPorRol(JSON.parse(usuarioGuardado).rol);
}

function redirigirPorRol(rol) {
    if (rol === 'CLIENTE')       window.location.href = 'dashboard-cliente.html';
    else if (rol === 'CAMARERO') window.location.href = 'dashboard-camarero.html';
    else if (rol === 'GERENTE')  window.location.href = 'dashboard-gerente.html';
    else                         window.location.href = 'dashboard.html';
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
        redirigirPorRol(usuario.rol);

    } catch {
        errorEl.textContent = 'No se pudo conectar con el servidor.';
    }
}