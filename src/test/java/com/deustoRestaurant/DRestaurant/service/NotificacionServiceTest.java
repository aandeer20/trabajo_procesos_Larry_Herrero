package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.NotificacionDAO;
import com.deustoRestaurant.DRestaurant.dto.NotificacionResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Notificacion;
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
class NotificacionServiceTest {

    @Mock private NotificacionDAO notificacionDAO;

    @InjectMocks
    private NotificacionService notificacionService;

    private Usuario destinatario;
    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        destinatario = new Usuario();
        destinatario.setId(1L);
        destinatario.setNombre("Carlos");
        destinatario.setRol(Rol.CLIENTE);

        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje("Tu reserva ha cambiado de estado.");
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());
    }

    // ── CREAR ──────────────────────────────────────────────────────────────

    @Test
    void crear_guardaNotificacion() {
        notificacionService.crear(destinatario, "Tu reserva ha cambiado de estado.");

        verify(notificacionDAO).save(any(Notificacion.class));
    }

    @Test
    void crear_estableceFechaCreacion() {
        when(notificacionDAO.save(any())).thenAnswer(inv -> {
            Notificacion n = inv.getArgument(0);
            assertNotNull(n.getFechaCreacion());
            assertFalse(n.isLeida());
            assertEquals("Mensaje de prueba", n.getMensaje());
            return n;
        });

        notificacionService.crear(destinatario, "Mensaje de prueba");
    }

    // ── OBTENER POR USUARIO ────────────────────────────────────────────────

    @Test
    void obtenerPorUsuario_devuelveLista() {
        when(notificacionDAO.findByDestinatarioIdOrderByFechaCreacionDesc(1L))
                .thenReturn(List.of(notificacion));

        List<NotificacionResponseDTO> result = notificacionService.obtenerPorUsuario(1L);

        assertEquals(1, result.size());
        assertEquals("Tu reserva ha cambiado de estado.", result.get(0).getMensaje());
        assertFalse(result.get(0).isLeida());
    }

    @Test
    void obtenerPorUsuario_sinNotificaciones_devueltaListaVacia() {
        when(notificacionDAO.findByDestinatarioIdOrderByFechaCreacionDesc(99L))
                .thenReturn(List.of());

        List<NotificacionResponseDTO> result = notificacionService.obtenerPorUsuario(99L);

        assertTrue(result.isEmpty());
    }

    // ── CONTAR NO LEIDAS ───────────────────────────────────────────────────

    @Test
    void contarNoLeidas_devuelveTotal() {
        when(notificacionDAO.countByDestinatarioIdAndLeida(1L, false)).thenReturn(3L);

        long total = notificacionService.contarNoLeidas(1L);

        assertEquals(3L, total);
    }

    @Test
    void contarNoLeidas_cero_siTodasLeidas() {
        when(notificacionDAO.countByDestinatarioIdAndLeida(1L, false)).thenReturn(0L);

        long total = notificacionService.contarNoLeidas(1L);

        assertEquals(0L, total);
    }

    // ── MARCAR LEIDA ───────────────────────────────────────────────────────

    @Test
    void marcarLeida_exitoso() {
        when(notificacionDAO.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionDAO.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificacionService.marcarLeida(1L);

        assertTrue(notificacion.isLeida());
        verify(notificacionDAO).save(notificacion);
    }

    @Test
    void marcarLeida_noExiste_noLanzaExcepcion() {
        when(notificacionDAO.findById(99L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> notificacionService.marcarLeida(99L));
        verify(notificacionDAO, never()).save(any());
    }

    // ── MARCAR TODAS LEIDAS ────────────────────────────────────────────────

    @Test
    void marcarTodasLeidas_marcaTodasLasNoLeidas() {
        Notificacion n2 = new Notificacion();
        n2.setId(2L);
        n2.setDestinatario(destinatario);
        n2.setLeida(false);

        when(notificacionDAO.findByDestinatarioIdAndLeida(1L, false))
                .thenReturn(List.of(notificacion, n2));

        notificacionService.marcarTodasLeidas(1L);

        assertTrue(notificacion.isLeida());
        assertTrue(n2.isLeida());
        verify(notificacionDAO).saveAll(anyList());
    }

    @Test
    void marcarTodasLeidas_sinNoLeidas_noGuarda() {
        when(notificacionDAO.findByDestinatarioIdAndLeida(1L, false)).thenReturn(List.of());

        notificacionService.marcarTodasLeidas(1L);

        verify(notificacionDAO).saveAll(List.of());
    }
}
