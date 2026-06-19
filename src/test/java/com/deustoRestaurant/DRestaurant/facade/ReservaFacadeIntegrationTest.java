package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dao.NotificacionDAO;
import com.deustoRestaurant.DRestaurant.dao.ReservaDAO;
import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservaFacadeIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReservaDAO reservaDAO;
    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private RestauranteDAO restauranteDAO;
    @Autowired private NotificacionDAO notificacionDAO;

    private Usuario cliente;
    private Usuario camarero;
    private Usuario gerente;
    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        notificacionDAO.deleteAll();
        reservaDAO.deleteAll();
        usuarioDAO.deleteAll();
        restauranteDAO.deleteAll();

        restaurante = new Restaurante();
        restaurante.setNombre("DeustoRestaurant");
        restaurante.setDireccion("Bilbao");
        restaurante.setAforoMaximoComida(20);
        restaurante.setAforoMaximoCena(15);
        restaurante.setActivo(true);
        restaurante = restauranteDAO.save(restaurante);

        cliente = new Usuario();
        cliente.setNombre("Carlos");
        cliente.setEmail("carlos@test.com");
        cliente.setPassword("1234");
        cliente.setRol(Rol.CLIENTE);
        cliente.setActivo(true);
        cliente = usuarioDAO.save(cliente);

        gerente = new Usuario();
        gerente.setNombre("Elena");
        gerente.setEmail("elena@test.com");
        gerente.setPassword("1234");
        gerente.setRol(Rol.GERENTE);
        gerente.setActivo(true);
        gerente.setRestaurante(restaurante);
        gerente = usuarioDAO.save(gerente);

        camarero = new Usuario();
        camarero.setNombre("Pedro");
        camarero.setEmail("pedro@test.com");
        camarero.setPassword("1234");
        camarero.setRol(Rol.CAMARERO);
        camarero.setActivo(true);
        camarero.setRestaurante(restaurante);
        camarero = usuarioDAO.save(camarero);
    }

    private Reserva crearReservaDirecta(EstadoReserva estado, Turno turno) {
        Reserva r = new Reserva();
        r.setFecha(LocalDate.now());
        r.setTurno(turno);
        r.setNumComensales(2);
        r.setEstado(estado);
        r.setCliente(cliente);
        r.setRestaurante(restaurante);
        return reservaDAO.save(r);
    }

    // ── CREAR ──────────────────────────────────────────────────────────────

    @Test
    void crear_exitoso_devuelve201_yNotificaGerente() throws Exception {
        Map<String, Object> request = Map.of(
                "fecha", LocalDate.now().toString(),
                "turno", "COMIDA",
                "numComensales", 2,
                "clienteId", cliente.getId(),
                "restauranteId", restaurante.getId()
        );

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.turno", is("COMIDA")))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")));

        // Gerente debe tener notificación
        mockMvc.perform(get("/api/notificaciones/usuario/" + gerente.getId() + "/no-leidas"))
                .andExpect(jsonPath("$.total", greaterThanOrEqualTo(1)));
    }

    @Test
    void crear_aforoLleno_devuelve400() throws Exception {
        for (int i = 0; i < 20; i++) {
            crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.COMIDA);
        }

        Map<String, Object> request = Map.of(
                "fecha", LocalDate.now().toString(),
                "turno", "COMIDA",
                "numComensales", 1,
                "clienteId", cliente.getId(),
                "restauranteId", restaurante.getId()
        );

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── CANCELAR ───────────────────────────────────────────────────────────

    @Test
    void cancelar_exitoso_devuelveEstadoCancelada() throws Exception {
        Reserva reserva = crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.CENA);

        mockMvc.perform(put("/api/reservas/" + reserva.getId() + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CANCELADA")));
    }

    // ── CAMBIAR ESTADO ─────────────────────────────────────────────────────

    @Test
    void cambiarEstado_aCompletada_notificaCliente() throws Exception {
        Reserva reserva = crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.CENA);

        Map<String, String> request = Map.of("estado", "COMPLETADA");

        mockMvc.perform(put("/api/reservas/" + reserva.getId() + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("COMPLETADA")));

        mockMvc.perform(get("/api/notificaciones/usuario/" + cliente.getId() + "/no-leidas"))
                .andExpect(jsonPath("$.total", is(1)));
    }

    @Test
    void cambiarEstado_aConfirmada() throws Exception {
        Reserva reserva = crearReservaDirecta(EstadoReserva.PENDIENTE, Turno.COMIDA);

        Map<String, String> request = Map.of("estado", "CONFIRMADA");

        mockMvc.perform(put("/api/reservas/" + reserva.getId() + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CONFIRMADA")));
    }

    // ── ASIGNAR CAMARERO ───────────────────────────────────────────────────

    @Test
    void asignarCamarero_exitoso_devuelveNombreCamarero() throws Exception {
        Reserva reserva = crearReservaDirecta(EstadoReserva.PENDIENTE, Turno.COMIDA);

        mockMvc.perform(put("/api/reservas/" + reserva.getId() + "/camarero/" + camarero.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCamarero", is("Pedro")));
    }

    // ── GET POR CLIENTE ────────────────────────────────────────────────────

    @Test
    void obtenerPorCliente_devuelveReservasDelCliente() throws Exception {
        crearReservaDirecta(EstadoReserva.PENDIENTE, Turno.COMIDA);

        mockMvc.perform(get("/api/reservas/cliente/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreCliente", is("Carlos")));
    }

    // ── GET POR RESTAURANTE ────────────────────────────────────────────────

    @Test
    void obtenerPorRestaurante_devuelveLista() throws Exception {
        crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.COMIDA);
        crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.CENA);

        mockMvc.perform(get("/api/reservas/restaurante/" + restaurante.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ── GET POR TURNO ──────────────────────────────────────────────────────

    @Test
    void obtenerPorRestauranteYTurno_filtrasCorrectamente() throws Exception {
        crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.COMIDA);
        crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.CENA);

        mockMvc.perform(get("/api/reservas/turno")
                        .param("restauranteId", restaurante.getId().toString())
                        .param("fecha", LocalDate.now().toString())
                        .param("turno", "COMIDA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].turno", is("COMIDA")));
    }

    // ── PENDIENTES ─────────────────────────────────────────────────────────

    @Test
    void obtenerPendientes_devuelveSoloPendientes() throws Exception {
        crearReservaDirecta(EstadoReserva.PENDIENTE, Turno.COMIDA);
        crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.CENA);

        mockMvc.perform(get("/api/reservas/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].estado", everyItem(is("PENDIENTE"))));
    }

    @Test
    void obtenerPendientesPorRestaurante_filtraPorRestaurante() throws Exception {
        crearReservaDirecta(EstadoReserva.PENDIENTE, Turno.COMIDA);
        crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.COMIDA);

        mockMvc.perform(get("/api/reservas/pendientes/restaurante/" + restaurante.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is("PENDIENTE")));
    }

    // ── GET POR CAMARERO ───────────────────────────────────────────────────

    @Test
    void obtenerPorCamarero_devuelveReservasAsignadas() throws Exception {
        Reserva reserva = crearReservaDirecta(EstadoReserva.CONFIRMADA, Turno.COMIDA);
        reserva.setCamarero(camarero);
        reservaDAO.save(reserva);

        mockMvc.perform(get("/api/reservas/camarero")
                        .param("camareroId", camarero.getId().toString())
                        .param("fecha", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
