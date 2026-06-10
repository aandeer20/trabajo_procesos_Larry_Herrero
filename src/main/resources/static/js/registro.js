// Redirige si ya hay sesión activa
const usuarioGuardado = sessionStorage.getItem('usuario');
if (usuarioGuardado) {
    window.location.href = 'dashboard-cliente.html';
}

async function submitRegistro(e) {
    e.preventDefault();
    const errorEl = document.getElementById('reg-error');
    errorEl.textContent = '';

    const password = document.getElementById('reg-password').value;
    const password2 = document.getElementById('reg-password2').value;

    if (password !== password2) {
        errorEl.textContent = 'Las contraseñas no coinciden.';
        return;
    }

    const body = {
        nombre: document.getElementById('reg-nombre').value.trim(),
        apellidos: document.getElementById('reg-apellidos').value.trim(),
        email: document.getElementById('reg-email').value.trim(),
        telefono: document.getElementById('reg-telefono').value.trim(),
        password: password,
        rol: 'CLIENTE'
    };

    try {
        const res = await fetch(`${API_BASE}/usuarios`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (res.status === 400) {
            errorEl.textContent = 'El email ya está registrado.';
            return;
        }
        if (!res.ok) {
            errorEl.textContent = 'Error del servidor. Inténtalo más tarde.';
            return;
        }

        const usuario = await res.json();
        sessionStorage.setItem('usuario', JSON.stringify(usuario));
        window.location.href = 'dashboard-cliente.html';

    } catch {
        errorEl.textContent = 'No se pudo conectar con el servidor.';
    }
}
