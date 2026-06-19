package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
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

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestauranteFacadeIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RestauranteDAO restauranteDAO;

    @BeforeEach
    void setUp() {
        restauranteDAO.deleteAll();
    }

    // ── CREAR ──────────────────────────────────────────────────────────────

    @Test
    void crear_exitoso_devuelve201() throws Exception {
        Map<String, Object> request = Map.of(
                "nombre", "DeustoRestaurant",
                "direccion", "Bilbao",
                "telefono", "944000001",
                "horarioComida", "13:00-16:00",
                "horarioCena", "20:00-23:00",
                "aforoMaximoComida", 30,
                "aforoMaximoCena", 20
        );

        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("DeustoRestaurant")))
                .andExpect(jsonPath("$.activo", is(true)))
                .andExpect(jsonPath("$.aforoMaximoComida", is(30)));
    }

    // ── OBTENER TODOS ──────────────────────────────────────────────────────

    @Test
    void obtenerTodos_devuelveLista() throws Exception {
        Restaurante r = new Restaurante();
        r.setNombre("TestRest");
        r.setDireccion("Madrid");
        r.setAforoMaximoComida(10);
        r.setAforoMaximoCena(10);
        r.setActivo(true);
        restauranteDAO.save(r);

        mockMvc.perform(get("/api/restaurantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // ── OBTENER ACTIVOS ────────────────────────────────────────────────────

    @Test
    void obtenerActivos_soloDevuelveActivos() throws Exception {
        Restaurante activo = new Restaurante();
        activo.setNombre("Activo");
        activo.setAforoMaximoComida(10);
        activo.setAforoMaximoCena(10);
        activo.setActivo(true);
        restauranteDAO.save(activo);

        Restaurante inactivo = new Restaurante();
        inactivo.setNombre("Inactivo");
        inactivo.setAforoMaximoComida(10);
        inactivo.setAforoMaximoCena(10);
        inactivo.setActivo(false);
        restauranteDAO.save(inactivo);

        mockMvc.perform(get("/api/restaurantes/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].activo", everyItem(is(true))));
    }

    // ── ACTUALIZAR ─────────────────────────────────────────────────────────

    @Test
    void actualizar_exitoso_devuelve200() throws Exception {
        Restaurante r = new Restaurante();
        r.setNombre("Original");
        r.setDireccion("Bilbao");
        r.setAforoMaximoComida(10);
        r.setAforoMaximoCena(10);
        r.setActivo(true);
        Restaurante saved = restauranteDAO.save(r);

        Map<String, Object> request = Map.of(
                "nombre", "Modificado",
                "direccion", "Bilbao",
                "aforoMaximoComida", 25,
                "aforoMaximoCena", 15
        );

        mockMvc.perform(put("/api/restaurantes/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Modificado")));
    }

    // ── ACTUALIZAR AFORO ───────────────────────────────────────────────────

    @Test
    void actualizarAforoComida_exitoso() throws Exception {
        Restaurante r = new Restaurante();
        r.setNombre("Rest");
        r.setAforoMaximoComida(10);
        r.setAforoMaximoCena(10);
        r.setActivo(true);
        Restaurante saved = restauranteDAO.save(r);

        Map<String, Integer> request = Map.of("aforo", 50);

        mockMvc.perform(put("/api/restaurantes/" + saved.getId() + "/aforo/comida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aforoMaximoComida", is(50)));
    }

    // ── DESACTIVAR ─────────────────────────────────────────────────────────

    @Test
    void desactivar_exitoso_devuelveActivoFalse() throws Exception {
        Restaurante r = new Restaurante();
        r.setNombre("ABorrar");
        r.setAforoMaximoComida(10);
        r.setAforoMaximoCena(10);
        r.setActivo(true);
        Restaurante saved = restauranteDAO.save(r);

        mockMvc.perform(delete("/api/restaurantes/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(false)));
    }
}
