// Protección de ruta: si no hay sesión, redirige al login
const usuario = JSON.parse(sessionStorage.getItem('usuario'));
if (!usuario) {
    window.location.href = 'login.html';
}

// Rellenar nombre en el navbar y bienvenida
document.getElementById('nav-nombre').textContent = usuario.nombre + ' ' + (usuario.apellidos || '');
document.getElementById('bienvenida-nombre').textContent = usuario.nombre;

// Subtítulo según rol
const subtitulos = {
    CLIENTE: '¿Qué quieres hacer hoy?',
    CAMARERO: 'Panel de camarero — ' + (usuario.nombreRestaurante || ''),
    GERENTE: 'Panel de gerente — ' + (usuario.nombreRestaurante || '')
};
document.getElementById('subtitulo').textContent = subtitulos[usuario.rol] || '';

// Mostrar tarjetas según rol
const rolCards = {
    CLIENTE: 'cards-cliente',
    CAMARERO: 'cards-camarero',
    GERENTE: 'cards-gerente'
};
const cardId = rolCards[usuario.rol];
if (cardId) {
    document.getElementById(cardId).style.display = 'flex';
}

function cerrarSesion() {
    sessionStorage.removeItem('usuario');
    window.location.href = 'index.html';
}
