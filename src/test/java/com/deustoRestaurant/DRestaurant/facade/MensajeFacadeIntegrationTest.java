package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dao.MensajeDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.entity.Mensaje;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MensajeFacadeIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MensajeDAO mensajeDAO;
    @Autowired private UsuarioDAO usuarioDAO;

    private Usuario remitente;
    private Usuario destinatario;

    @BeforeEach
    void setUp() {
        mensajeDAO.deleteAll();
        usuarioDAO.deleteAll();

        remitente = new Usuario();
        remitente.setNombre("Carlos");
        remitente.setEmail("carlos@test.com");
        remitente.setPassword("1234");
        remitente.setRol(Rol.CLIENTE);
        remitente.setActivo(true);
        remitente = usuarioDAO.save(remitente);

        destinatario = new Usuario();
        destinatario.setNombre("Pedro");
        destinatario.setEmail("pedro@test.com");
        destinatario.setPassword("1234");
        destinatario.setRol(Rol.CAMARERO);
        destinatario.setActivo(true);
        destinatario = usuarioDAO.save(destinatario);
    }

    // ── ENVIAR ─────────────────────────────────────────────────────────────

    @Test
    void enviar_exitoso_devuelve201() throws Exception {
        Map<String, Object> request = Map.of(
                "contenido", "Buenas, quisiera confirmar mi mesa.",
                "remitenteId", remitente.getId(),
                "destinatarioId", destinatario.getId()
        );

        mockMvc.perform(post("/api/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contenido", is("Buenas, quisiera confirmar mi mesa.")))
                .andExpect(jsonPath("$.nombreRemitente", is("Carlos")))
                .andExpect(jsonPath("$.leido", is(false)));
    }

    @Test
    void enviar_remitenteInexistente_devuelve404() throws Exception {
        Map<String, Object> request = Map.of(
                "contenido", "Hola",
                "remitenteId", 9999,
                "destinatarioId", destinatario.getId()
        );

        mockMvc.perform(post("/api/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ── OBTENER RECIBIDOS ──────────────────────────────────────────────────

    @Test
    void obtenerRecibidos_devuelveMensajesDelDestinatario() throws Exception {
        Mensaje m = new Mensaje();
        m.setContenido("Hola");
        m.setFechaCreacion(LocalDateTime.now());
        m.setLeido(false);
        m.setRemitente(remitente);
        m.setDestinatario(destinatario);
        mensajeDAO.save(m);

        mockMvc.perform(get("/api/mensajes/recibidos/" + destinatario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreRemitente", is("Carlos")));
    }

    // ── OBTENER NO LEIDOS ──────────────────────────────────────────────────

    @Test
    void obtenerNoLeidos_devuelveSoloPendientes() throws Exception {
        Mensaje noLeido = new Mensaje();
        noLeido.setContenido("Sin leer");
        noLeido.setFechaCreacion(LocalDateTime.now());
        noLeido.setLeido(false);
        noLeido.setRemitente(remitente);
        noLeido.setDestinatario(destinatario);
        mensajeDAO.save(noLeido);

        Mensaje leido = new Mensaje();
        leido.setContenido("Ya leído");
        leido.setFechaCreacion(LocalDateTime.now());
        leido.setLeido(true);
        leido.setRemitente(remitente);
        leido.setDestinatario(destinatario);
        mensajeDAO.save(leido);

        mockMvc.perform(get("/api/mensajes/noleidos/" + destinatario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].leido", is(false)));
    }

    // ── MARCAR LEÍDO ───────────────────────────────────────────────────────

    @Test
    void marcarLeido_exitoso_devuelveLeidoTrue() throws Exception {
        Mensaje m = new Mensaje();
        m.setContenido("Marcar");
        m.setFechaCreacion(LocalDateTime.now());
        m.setLeido(false);
        m.setRemitente(remitente);
        m.setDestinatario(destinatario);
        Mensaje saved = mensajeDAO.save(m);

        mockMvc.perform(put("/api/mensajes/" + saved.getId() + "/leido"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leido", is(true)));
    }

    // ── CONVERSACIÓN ───────────────────────────────────────────────────────

    @Test
    void obtenerConversacion_devuelveMensajesEntreAmbos() throws Exception {
        Mensaje m = new Mensaje();
        m.setContenido("Mensaje entre Carlos y Pedro");
        m.setFechaCreacion(LocalDateTime.now());
        m.setLeido(false);
        m.setRemitente(remitente);
        m.setDestinatario(destinatario);
        mensajeDAO.save(m);

        mockMvc.perform(get("/api/mensajes/conversacion")
                        .param("remitenteId", remitente.getId().toString())
                        .param("destinatarioId", destinatario.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
