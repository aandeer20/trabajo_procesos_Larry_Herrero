package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioResponseDTO;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private RestauranteDAO restauranteDAO;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Carlos");
        usuario.setApellidos("García");
        usuario.setEmail("carlos@email.com");
        usuario.setPassword("1234");
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNombre("DeustoRestaurant");
    }

    // ── REGISTRAR ──────────────────────────────────────────────────────────

    @Test
    void registrar_exitoso() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre("Carlos");
        dto.setApellidos("García");
        dto.setEmail("carlos@email.com");
        dto.setPassword("1234");
        dto.setRol(Rol.CLIENTE);

        when(usuarioDAO.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(usuarioDAO.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO result = usuarioService.registrar(dto);

        assertNotNull(result);
        assertEquals("Carlos", result.getNombre());
        assertEquals(Rol.CLIENTE, result.getRol());
        verify(usuarioDAO).save(any(Usuario.class));
    }

    @Test
    void registrar_emailDuplicado_lanzaExcepcion() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setEmail("carlos@email.com");

        when(usuarioDAO.findByEmail(dto.getEmail())).thenReturn(Optional.of(usuario));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.registrar(dto));
        assertEquals("Ya existe un usuario con ese email", ex.getMessage());
        verify(usuarioDAO, never()).save(any());
    }

    @Test
    void registrar_conRestaurante_asignaCorrectamente() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombre("Pedro");
        dto.setEmail("pedro@email.com");
        dto.setPassword("1234");
        dto.setRol(Rol.CAMARERO);
        dto.setRestauranteId(1L);

        Usuario camarero = new Usuario();
        camarero.setId(2L);
        camarero.setNombre("Pedro");
        camarero.setRol(Rol.CAMARERO);
        camarero.setRestaurante(restaurante);

        when(usuarioDAO.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioDAO.save(any(Usuario.class))).thenReturn(camarero);

        UsuarioResponseDTO result = usuarioService.registrar(dto);

        assertEquals("DeustoRestaurant", result.getNombreRestaurante());
        verify(restauranteDAO).findById(1L);
    }

    // ── LOGIN ──────────────────────────────────────────────────────────────

    @Test
    void login_exitoso() {
        when(usuarioDAO.findByEmail("carlos@email.com")).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO result = usuarioService.login("carlos@email.com", "1234");

        assertNotNull(result);
        assertEquals("Carlos", result.getNombre());
    }

    @Test
    void login_emailNoEncontrado_lanzaExcepcion() {
        when(usuarioDAO.findByEmail("noexiste@email.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("noexiste@email.com", "1234"));
        assertEquals("Credenciales incorrectas", ex.getMessage());
    }

    @Test
    void login_passwordIncorrecta_lanzaExcepcion() {
        when(usuarioDAO.findByEmail("carlos@email.com")).thenReturn(Optional.of(usuario));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("carlos@email.com", "wrongpass"));
        assertEquals("Credenciales incorrectas", ex.getMessage());
    }

    @Test
    void login_usuarioDesactivado_lanzaExcepcion() {
        usuario.setActivo(false);
        when(usuarioDAO.findByEmail("carlos@email.com")).thenReturn(Optional.of(usuario));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.login("carlos@email.com", "1234"));
        assertEquals("Usuario desactivado", ex.getMessage());
    }

    // ── OBTENER POR ROL ────────────────────────────────────────────────────

    @Test
    void obtenerPorRol_devuelveListaFiltrada() {
        when(usuarioDAO.findByRol(Rol.CAMARERO)).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> result = usuarioService.obtenerPorRol(Rol.CAMARERO);

        assertEquals(1, result.size());
        verify(usuarioDAO).findByRol(Rol.CAMARERO);
    }

    // ── ASIGNAR RESTAURANTE ────────────────────────────────────────────────

    @Test
    void asignarRestaurante_exitoso() {
        usuario.setRestaurante(restaurante);
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(usuarioDAO.save(any())).thenReturn(usuario);

        UsuarioResponseDTO result = usuarioService.asignarRestaurante(1L, 1L);

        assertEquals("DeustoRestaurant", result.getNombreRestaurante());
    }

    @Test
    void asignarRestaurante_usuarioNoEncontrado_lanzaExcepcion() {
        when(usuarioDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.asignarRestaurante(99L, 1L));
    }

    // ── DESACTIVAR ─────────────────────────────────────────────────────────

    @Test
    void desactivar_exitoso() {
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioDAO.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO result = usuarioService.desactivar(1L);

        assertFalse(result.isActivo());
    }

    @Test
    void desactivar_noEncontrado_lanzaExcepcion() {
        when(usuarioDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.desactivar(99L));
    }
}
