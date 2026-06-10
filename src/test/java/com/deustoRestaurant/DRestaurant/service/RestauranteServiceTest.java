package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestauranteServiceTest {

    @Mock private RestauranteDAO restauranteDAO;
    @Mock private UsuarioDAO usuarioDAO;

    @InjectMocks
    private RestauranteService restauranteService;

    private Restaurante restaurante;
    private Usuario gerente;

    @BeforeEach
    void setUp() {
        gerente = new Usuario();
        gerente.setId(1L);
        gerente.setNombre("Elena");
        gerente.setRol(Rol.GERENTE);

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNombre("DeustoRestaurant");
        restaurante.setDireccion("Bilbao");
        restaurante.setAforoMaximoComida(30);
        restaurante.setAforoMaximoCena(20);
        restaurante.setActivo(true);
        restaurante.setGerente(gerente);
    }

    // ── CREAR ──────────────────────────────────────────────────────────────

    @Test
    void crear_sinGerente_exitoso() {
        RestauranteRequestDTO dto = buildDTO();
        dto.setGerenteId(null);

        when(restauranteDAO.save(any())).thenReturn(restaurante);

        RestauranteResponseDTO result = restauranteService.crear(dto);

        assertNotNull(result);
        assertEquals("DeustoRestaurant", result.getNombre());
        verify(usuarioDAO, never()).findById(any());
    }

    @Test
    void crear_conGerente_exitoso() {
        RestauranteRequestDTO dto = buildDTO();
        dto.setGerenteId(1L);

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(gerente));
        when(restauranteDAO.save(any())).thenReturn(restaurante);

        RestauranteResponseDTO result = restauranteService.crear(dto);

        assertEquals("Elena", result.getNombreGerente());
        verify(usuarioDAO).findById(1L);
    }

    @Test
    void crear_gerenteNoEncontrado_lanzaExcepcion() {
        RestauranteRequestDTO dto = buildDTO();
        dto.setGerenteId(99L);

        when(usuarioDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> restauranteService.crear(dto));
        verify(restauranteDAO, never()).save(any());
    }

    // ── ACTUALIZAR ─────────────────────────────────────────────────────────

    @Test
    void actualizar_exitoso() {
        RestauranteRequestDTO dto = buildDTO();
        dto.setNombre("NuevoNombre");
        dto.setAforoMaximoComida(50);

        Restaurante actualizado = new Restaurante();
        actualizado.setId(1L);
        actualizado.setNombre("NuevoNombre");
        actualizado.setAforoMaximoComida(50);
        actualizado.setAforoMaximoCena(20);

        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteDAO.save(any())).thenReturn(actualizado);

        RestauranteResponseDTO result = restauranteService.actualizar(1L, dto);

        assertEquals("NuevoNombre", result.getNombre());
        assertEquals(50, result.getAforoMaximoComida());
    }

    @Test
    void actualizar_noEncontrado_lanzaExcepcion() {
        when(restauranteDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> restauranteService.actualizar(99L, buildDTO()));
    }

    // ── ACTUALIZAR AFORO ───────────────────────────────────────────────────

    @Test
    void actualizarAforoComida_exitoso() {
        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteDAO.save(any())).thenAnswer(inv -> {
            Restaurante r = inv.getArgument(0);
            return r;
        });

        RestauranteResponseDTO result = restauranteService.actualizarAforoComida(1L, 50);

        assertEquals(50, result.getAforoMaximoComida());
    }

    @Test
    void actualizarAforoCena_exitoso() {
        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteDAO.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RestauranteResponseDTO result = restauranteService.actualizarAforoCena(1L, 35);

        assertEquals(35, result.getAforoMaximoCena());
    }

    @Test
    void actualizarAforo_noEncontrado_lanzaExcepcion() {
        when(restauranteDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> restauranteService.actualizarAforoComida(99L, 10));
    }

    // ── DESACTIVAR ─────────────────────────────────────────────────────────

    @Test
    void desactivar_exitoso() {
        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(restauranteDAO.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RestauranteResponseDTO result = restauranteService.desactivar(1L);

        assertFalse(result.isActivo());
    }

    // ── CONSULTAS ──────────────────────────────────────────────────────────

    @Test
    void obtenerTodos_devuelveListaCompleta() {
        when(restauranteDAO.findAll()).thenReturn(List.of(restaurante));

        List<RestauranteResponseDTO> result = restauranteService.obtenerTodos();

        assertEquals(1, result.size());
        assertEquals("DeustoRestaurant", result.get(0).getNombre());
    }

    @Test
    void obtenerActivos_soloActivos() {
        when(restauranteDAO.findByActivo(true)).thenReturn(List.of(restaurante));

        List<RestauranteResponseDTO> result = restauranteService.obtenerActivos();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActivo());
    }

    // ── HELPER ─────────────────────────────────────────────────────────────

    private RestauranteRequestDTO buildDTO() {
        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNombre("DeustoRestaurant");
        dto.setDireccion("Bilbao");
        dto.setTelefono("944000000");
        dto.setHorarioComida("13:00-16:00");
        dto.setHorarioCena("20:00-23:00");
        dto.setAforoMaximoComida(30);
        dto.setAforoMaximoCena(20);
        return dto;
    }
}
