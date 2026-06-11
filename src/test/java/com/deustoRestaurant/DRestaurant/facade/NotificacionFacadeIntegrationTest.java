package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dao.NotificacionDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.entity.Notificacion;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificacionFacadeIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private NotificacionDAO notificacionDAO;
    @Autowired private UsuarioDAO usuarioDAO;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        notificacionDAO.deleteAll();
        usuarioDAO.deleteAll();

        usuario = new Usuario();
        usuario.setNombre("Carlos");
        usuario.setEmail("carlos@test.com");
        usuario.setPassword("1234");
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        usuario = usuarioDAO.save(usuario);
    }

    private Notificacion crearNotificacion(String mensaje, boolean leida) {
        Notificacion n = new Notificacion();
        n.setDestinatario(usuario);
        n.setMensaje(mensaje);
        n.setLeida(leida);
        n.setFechaCreacion(LocalDateTime.now());
        return notificacionDAO.save(n);
    }

    // ── OBTENER POR USUARIO ────────────────────────────────────────────────

    @Test
    void obtenerPorUsuario_devuelveLista() throws Exception {
        crearNotificacion("Tu reserva ha cambiado.", false);
        crearNotificacion("Reserva confirmada.", true);

        mockMvc.perform(get("/api/notificaciones/usuario/" + usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void obtenerPorUsuario_sinNotificaciones_devuelveListaVacia() throws Exception {
        mockMvc.perform(get("/api/notificaciones/usuario/" + usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── CONTAR NO LEIDAS ───────────────────────────────────────────────────

    @Test
    void contarNoLeidas_devuelveTotalCorrecto() throws Exception {
        crearNotificacion("Msg 1", false);
        crearNotificacion("Msg 2", false);
        crearNotificacion("Msg 3", true);

        mockMvc.perform(get("/api/notificaciones/usuario/" + usuario.getId() + "/no-leidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(2)));
    }

    @Test
    void contarNoLeidas_cero_siTodasLeidas() throws Exception {
        crearNotificacion("Ya leída", true);

        mockMvc.perform(get("/api/notificaciones/usuario/" + usuario.getId() + "/no-leidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(0)));
    }

    // ── MARCAR LEIDA ───────────────────────────────────────────────────────

    @Test
    void marcarLeida_exitoso_marcaComoLeida() throws Exception {
        Notificacion n = crearNotificacion("Pendiente de leer", false);

        mockMvc.perform(put("/api/notificaciones/" + n.getId() + "/leer"))
                .andExpect(status().isOk());

        // Verificar que quedó marcada
        mockMvc.perform(get("/api/notificaciones/usuario/" + usuario.getId() + "/no-leidas"))
                .andExpect(jsonPath("$.total", is(0)));
    }

    // ── MARCAR TODAS LEIDAS ────────────────────────────────────────────────

    @Test
    void marcarTodasLeidas_poneTodasALeido() throws Exception {
        crearNotificacion("Notif 1", false);
        crearNotificacion("Notif 2", false);
        crearNotificacion("Notif 3", false);

        mockMvc.perform(put("/api/notificaciones/usuario/" + usuario.getId() + "/leer-todas"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notificaciones/usuario/" + usuario.getId() + "/no-leidas"))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    void marcarTodasLeidas_sinNoLeidas_noFalla() throws Exception {
        crearNotificacion("Ya leída", true);

        mockMvc.perform(put("/api/notificaciones/usuario/" + usuario.getId() + "/leer-todas"))
                .andExpect(status().isOk());
    }
}
