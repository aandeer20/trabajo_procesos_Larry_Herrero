package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dao.NotificacionDAO;
import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
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
class UsuarioFacadeIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private RestauranteDAO restauranteDAO;
    @Autowired private NotificacionDAO notificacionDAO;

    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        notificacionDAO.deleteAll();
        usuarioDAO.deleteAll();
        restauranteDAO.deleteAll();

        restaurante = new Restaurante();
        restaurante.setNombre("DeustoRestaurant");
        restaurante.setAforoMaximoComida(20);
        restaurante.setAforoMaximoCena(15);
        restaurante.setActivo(true);
        restaurante = restauranteDAO.save(restaurante);
    }

    // ── REGISTRAR ──────────────────────────────────────────────────────────

    @Test
    void registrar_exitoso_devuelve201() throws Exception {
        Map<String, Object> request = Map.of(
                "nombre", "Carlos",
                "apellidos", "García",
                "email", "carlos@test.com",
                "password", "1234",
                "telefono", "600000001",
                "rol", "CLIENTE"
        );

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Carlos")))
                .andExpect(jsonPath("$.rol", is("CLIENTE")));
    }

    @Test
    void registrar_emailDuplicado_devuelve400() throws Exception {
        Usuario u = new Usuario();
        u.setNombre("Carlos");
        u.setEmail("duplicado@test.com");
        u.setPassword("1234");
        u.setRol(Rol.CLIENTE);
        u.setActivo(true);
        usuarioDAO.save(u);

        Map<String, Object> request = Map.of(
                "nombre", "Otro",
                "email", "duplicado@test.com",
                "password", "4321",
                "rol", "CLIENTE"
        );

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── LOGIN ──────────────────────────────────────────────────────────────

    @Test
    void login_exitoso_devuelveUsuario() throws Exception {
        Usuario u = new Usuario();
        u.setNombre("Ana");
        u.setApellidos("López");
        u.setEmail("ana@test.com");
        u.setPassword("pass123");
        u.setRol(Rol.CLIENTE);
        u.setActivo(true);
        usuarioDAO.save(u);

        Map<String, String> request = Map.of("email", "ana@test.com", "password", "pass123");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Ana")))
                .andExpect(jsonPath("$.rol", is("CLIENTE")));
    }

    @Test
    void login_credencialesIncorrectas_devuelve404() throws Exception {
        Map<String, String> request = Map.of("email", "noexiste@test.com", "password", "wrong");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_passwordIncorrecta_devuelve404() throws Exception {
        Usuario u = new Usuario();
        u.setNombre("Luis");
        u.setEmail("luis@test.com");
        u.setPassword("correcta");
        u.setRol(Rol.CLIENTE);
        u.setActivo(true);
        usuarioDAO.save(u);

        Map<String, String> request = Map.of("email", "luis@test.com", "password", "incorrecta");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ── GET POR ROL ────────────────────────────────────────────────────────

    @Test
    void obtenerPorRol_devuelveListaFiltrada() throws Exception {
        Usuario camarero = new Usuario();
        camarero.setNombre("Luis");
        camarero.setEmail("luis@test.com");
        camarero.setPassword("pass");
        camarero.setRol(Rol.CAMARERO);
        camarero.setActivo(true);
        usuarioDAO.save(camarero);

        mockMvc.perform(get("/api/usuarios/rol/CAMARERO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].rol", is("CAMARERO")));
    }

    // ── GET CAMAREROS POR RESTAURANTE ──────────────────────────────────────

    @Test
    void obtenerCamarerosPorRestaurante_devuelveListaFiltrada() throws Exception {
        Usuario camarero = new Usuario();
        camarero.setNombre("Marcos");
        camarero.setEmail("marcos@test.com");
        camarero.setPassword("1234");
        camarero.setRol(Rol.CAMARERO);
        camarero.setActivo(true);
        camarero.setRestaurante(restaurante);
        usuarioDAO.save(camarero);

        mockMvc.perform(get("/api/usuarios/restaurante/" + restaurante.getId() + "/camareros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Marcos")));
    }

    // ── ACTUALIZAR ─────────────────────────────────────────────────────────

    @Test
    void actualizar_exitoso_devuelve200() throws Exception {
        Usuario u = new Usuario();
        u.setNombre("Original");
        u.setEmail("orig@test.com");
        u.setPassword("1234");
        u.setRol(Rol.CLIENTE);
        u.setActivo(true);
        Usuario saved = usuarioDAO.save(u);

        Map<String, Object> request = Map.of(
                "nombre", "Modificado",
                "telefono", "611000000"
        );

        mockMvc.perform(put("/api/usuarios/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Modificado")));
    }

    @Test
    void actualizar_usuarioNoExiste_devuelve404() throws Exception {
        Map<String, Object> request = Map.of("nombre", "Nuevo");

        mockMvc.perform(put("/api/usuarios/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ── ACTIVAR ────────────────────────────────────────────────────────────

    @Test
    void activar_exitoso_devuelveActivoTrue() throws Exception {
        Usuario u = new Usuario();
        u.setNombre("Inactivo");
        u.setEmail("inact@test.com");
        u.setPassword("1234");
        u.setRol(Rol.CLIENTE);
        u.setActivo(false);
        Usuario saved = usuarioDAO.save(u);

        mockMvc.perform(put("/api/usuarios/" + saved.getId() + "/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(true)));
    }

    // ── DESACTIVAR ─────────────────────────────────────────────────────────

    @Test
    void desactivar_exitoso_devuelve200() throws Exception {
        Usuario u = new Usuario();
        u.setNombre("Maria");
        u.setEmail("maria@test.com");
        u.setPassword("pass");
        u.setRol(Rol.CLIENTE);
        u.setActivo(true);
        Usuario saved = usuarioDAO.save(u);

        mockMvc.perform(delete("/api/usuarios/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo", is(false)));
    }

    // ── ASIGNAR RESTAURANTE ────────────────────────────────────────────────

    @Test
    void asignarRestaurante_exitoso_devuelveNombreRestaurante() throws Exception {
        Usuario u = new Usuario();
        u.setNombre("Camarero");
        u.setEmail("cam@test.com");
        u.setPassword("1234");
        u.setRol(Rol.CAMARERO);
        u.setActivo(true);
        Usuario saved = usuarioDAO.save(u);

        mockMvc.perform(put("/api/usuarios/" + saved.getId() + "/restaurante/" + restaurante.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreRestaurante", is("DeustoRestaurant")));
    }
}
