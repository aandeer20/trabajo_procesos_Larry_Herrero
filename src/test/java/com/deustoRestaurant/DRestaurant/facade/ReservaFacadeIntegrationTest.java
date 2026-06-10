package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dao.ReservaDAO;
import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservaFacadeIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReservaDAO reservaDAO;
    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private RestauranteDAO restauranteDAO;

    private Usuario cliente;
    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        reservaDAO.deleteAll();
        usuarioDAO.deleteAll();
        restauranteDAO.deleteAll();

        cliente = new Usuario();
        cliente.setNombre("Carlos");
        cliente.setEmail("carlos@test.com");
        cliente.setPassword("1234");
        cliente.setRol(Rol.CLIENTE);
        cliente.setActivo(true);
        cliente = usuarioDAO.save(cliente);

        restaurante = new Restaurante();
        restaurante.setNombre("DeustoRestaurant");
        restaurante.setDireccion("Bilbao");
        restaurante.setAforoMaximoComida(20);
        restaurante.setAforoMaximoCena(15);
        restaurante.setActivo(true);
        restaurante = restauranteDAO.save(restaurante);
    }

    // ── CREAR ──────────────────────────────────────────────────────────────

    @Test
    void crear_exitoso_devuelve201() throws Exception {
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
    }

    @Test
    void crear_aforoLleno_devuelve400() throws Exception {
        // Fill up comida capacity
        for (int i = 0; i < 20; i++) {
            Reserva r = new Reserva();
            r.setFecha(LocalDate.now());
            r.setTurno(Turno.COMIDA);
            r.setNumComensales(1);
            r.setEstado(EstadoReserva.CONFIRMADA);
            r.setCliente(cliente);
            r.setRestaurante(restaurante);
            reservaDAO.save(r);
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
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now());
        reserva.setTurno(Turno.CENA);
        reserva.setNumComensales(3);
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setCliente(cliente);
        reserva.setRestaurante(restaurante);
        Reserva saved = reservaDAO.save(reserva);

        mockMvc.perform(put("/api/reservas/" + saved.getId() + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CANCELADA")));
    }

    // ── GET POR CLIENTE ────────────────────────────────────────────────────

    @Test
    void obtenerPorCliente_devuelveReservasDelCliente() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now());
        reserva.setTurno(Turno.COMIDA);
        reserva.setNumComensales(2);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setCliente(cliente);
        reserva.setRestaurante(restaurante);
        reservaDAO.save(reserva);

        mockMvc.perform(get("/api/reservas/cliente/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreCliente", is("Carlos")));
    }

    // ── CAMBIAR ESTADO ─────────────────────────────────────────────────────

    @Test
    void cambiarEstado_aCompletada() throws Exception {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDate.now());
        reserva.setTurno(Turno.CENA);
        reserva.setNumComensales(2);
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setCliente(cliente);
        reserva.setRestaurante(restaurante);
        Reserva saved = reservaDAO.save(reserva);

        Map<String, String> request = Map.of("estado", "COMPLETADA");

        mockMvc.perform(put("/api/reservas/" + saved.getId() + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("COMPLETADA")));
    }
}
