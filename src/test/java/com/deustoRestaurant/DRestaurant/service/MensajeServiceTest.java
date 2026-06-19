package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.MensajeDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.MensajeRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.MensajeResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Mensaje;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock private MensajeDAO mensajeDAO;
    @Mock private UsuarioDAO usuarioDAO;

    @InjectMocks
    private MensajeService mensajeService;

    private Usuario remitente;
    private Usuario destinatario;
    private Mensaje mensaje;

    @BeforeEach
    void setUp() {
        remitente = new Usuario();
        remitente.setId(1L);
        remitente.setNombre("Carlos");
        remitente.setRol(Rol.CLIENTE);

        destinatario = new Usuario();
        destinatario.setId(2L);
        destinatario.setNombre("Pedro");
        destinatario.setRol(Rol.CAMARERO);

        mensaje = new Mensaje();
        mensaje.setId(1L);
        mensaje.setContenido("Hola, quisiera confirmar mi reserva.");
        mensaje.setFechaCreacion(LocalDateTime.now());
        mensaje.setLeido(false);
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
    }

    // ── ENVIAR ─────────────────────────────────────────────────────────────

    @Test
    void enviar_exitoso() {
        MensajeRequestDTO dto = buildDTO();

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(remitente));
        when(usuarioDAO.findById(2L)).thenReturn(Optional.of(destinatario));
        when(mensajeDAO.save(any())).thenReturn(mensaje);

        MensajeResponseDTO result = mensajeService.enviar(dto);

        assertNotNull(result);
        assertEquals("Carlos", result.getNombreRemitente());
        assertEquals("Pedro", result.getNombreDestinatario());
        assertEquals("Hola, quisiera confirmar mi reserva.", result.getContenido());
        assertFalse(result.isLeido());
        verify(mensajeDAO).save(any());
    }

    @Test
    void enviar_remitenteNoEncontrado_lanzaExcepcion() {
        MensajeRequestDTO dto = buildDTO();
        when(usuarioDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mensajeService.enviar(dto));
        verify(mensajeDAO, never()).save(any());
    }

    @Test
    void enviar_destinatarioNoEncontrado_lanzaExcepcion() {
        MensajeRequestDTO dto = buildDTO();
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(remitente));
        when(usuarioDAO.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mensajeService.enviar(dto));
        verify(mensajeDAO, never()).save(any());
    }

    @Test
    void enviar_estableceFechaCreacion() {
        MensajeRequestDTO dto = buildDTO();

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(remitente));
        when(usuarioDAO.findById(2L)).thenReturn(Optional.of(destinatario));
        when(mensajeDAO.save(any())).thenAnswer(inv -> {
            Mensaje m = inv.getArgument(0);
            assertNotNull(m.getFechaCreacion(), "fechaCreacion debe ser asignada por el servicio");
            return mensaje;
        });

        mensajeService.enviar(dto);
    }

    // ── OBTENER RECIBIDOS ──────────────────────────────────────────────────

    @Test
    void obtenerRecibidos_devuelveMensajesDelDestinatario() {
        when(mensajeDAO.findByDestinatarioId(2L)).thenReturn(List.of(mensaje));

        List<MensajeResponseDTO> result = mensajeService.obtenerRecibidos(2L);

        assertEquals(1, result.size());
        assertEquals("Carlos", result.get(0).getNombreRemitente());
    }

    @Test
    void obtenerRecibidos_sinMensajes_devuelveLista() {
        when(mensajeDAO.findByDestinatarioId(99L)).thenReturn(List.of());

        List<MensajeResponseDTO> result = mensajeService.obtenerRecibidos(99L);

        assertTrue(result.isEmpty());
    }

    // ── OBTENER NO LEIDOS ──────────────────────────────────────────────────

    @Test
    void obtenerNoLeidos_soloPendientes() {
        when(mensajeDAO.findByDestinatarioIdAndLeido(2L, false)).thenReturn(List.of(mensaje));

        List<MensajeResponseDTO> result = mensajeService.obtenerNoLeidos(2L);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isLeido());
    }

    // ── OBTENER CONVERSACIÓN ───────────────────────────────────────────────

    @Test
    void obtenerConversacion_devuelveMensajesEntreUsuarios() {
        when(mensajeDAO.findByRemitenteIdAndDestinatarioId(1L, 2L)).thenReturn(List.of(mensaje));

        List<MensajeResponseDTO> result = mensajeService.obtenerConversacion(1L, 2L);

        assertEquals(1, result.size());
        assertEquals("Hola, quisiera confirmar mi reserva.", result.get(0).getContenido());
    }

    // ── MARCAR LEIDO ───────────────────────────────────────────────────────

    @Test
    void marcarLeido_exitoso() {
        when(mensajeDAO.findById(1L)).thenReturn(Optional.of(mensaje));
        when(mensajeDAO.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MensajeResponseDTO result = mensajeService.marcarLeido(1L);

        assertTrue(result.isLeido());
    }

    @Test
    void marcarLeido_noEncontrado_lanzaExcepcion() {
        when(mensajeDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mensajeService.marcarLeido(99L));
    }

    // ── HELPER ─────────────────────────────────────────────────────────────

    private MensajeRequestDTO buildDTO() {
        MensajeRequestDTO dto = new MensajeRequestDTO();
        dto.setContenido("Hola, quisiera confirmar mi reserva.");
        dto.setRemitenteId(1L);
        dto.setDestinatarioId(2L);
        return dto;
    }
}
