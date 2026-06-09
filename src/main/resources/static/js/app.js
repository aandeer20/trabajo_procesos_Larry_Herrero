const API_BASE = '/api';

function abrirModal(tipo) {
    document.getElementById('modal-' + tipo).classList.add('active');
}

function cerrarModal(tipo) {
    document.getElementById('modal-' + tipo).classList.remove('active');
}

function cambiarModal(desde, hacia) {
    cerrarModal(desde);
    abrirModal(hacia);
}

// Cerrar modal al hacer clic fuera
document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function(e) {
        if (e.target === this) {
            this.classList.remove('active');
        }
    });
});

async function submitLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const errorEl = document.getElementById('login-error');
    errorEl.style.display = 'none';

    try {
        const res = await fetch(`${API_BASE}/usuarios/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {
            method: 'POST'
        });
        if (!res.ok) throw new Error();
        const usuario = await res.json();
        sessionStorage.setItem('usuario', JSON.stringify(usuario));
        cerrarModal('login');
        alert(`¡Bienvenido, ${usuario.nombre}!`);
    } catch {
        errorEl.style.display = 'block';
    }
}

async function submitRegistro(e) {
    e.preventDefault();
    const errorEl = document.getElementById('reg-error');
    errorEl.style.display = 'none';

    const body = {
        nombre: document.getElementById('reg-nombre').value,
        apellidos: document.getElementById('reg-apellidos').value,
        email: document.getElementById('reg-email').value,
        telefono: document.getElementById('reg-telefono').value,
        password: document.getElementById('reg-password').value,
        rol: 'CLIENTE'
    };

    try {
        const res = await fetch(`${API_BASE}/usuarios`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (!res.ok) throw new Error();
        const usuario = await res.json();
        sessionStorage.setItem('usuario', JSON.stringify(usuario));
        cerrarModal('registro');
        alert(`¡Cuenta creada, ${usuario.nombre}! Ya puedes reservar tu mesa.`);
    } catch {
        errorEl.style.display = 'block';
    }
}
