package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.ReservaDAO;
import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.ReservaRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.ReservaResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock private ReservaDAO reservaDAO;
    @Mock private UsuarioDAO usuarioDAO;
    @Mock private RestauranteDAO restauranteDAO;

    @InjectMocks
    private ReservaService reservaService;

    private Restaurante restaurante;
    private Usuario cliente;
    private Usuario camarero;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNombre("DeustoRestaurant");
        restaurante.setAforoMaximoComida(20);
        restaurante.setAforoMaximoCena(15);

        cliente = new Usuario();
        cliente.setId(1L);
        cliente.setNombre("Carlos");
        cliente.setRol(Rol.CLIENTE);

        camarero = new Usuario();
        camarero.setId(2L);
        camarero.setNombre("Pedro");
        camarero.setRol(Rol.CAMARERO);

        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFecha(LocalDate.now());
        reserva.setTurno(Turno.COMIDA);
        reserva.setNumComensales(2);
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reserva.setCliente(cliente);
        reserva.setRestaurante(restaurante);
    }

    // ── CREAR ──────────────────────────────────────────────────────────────

    @Test
    void crear_turnoComida_exitoso() {
        ReservaRequestDTO dto = buildDTO(Turno.COMIDA);

        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(reservaDAO.countByRestauranteIdAndFechaAndTurnoAndEstadoNot(
                1L, dto.getFecha(), Turno.COMIDA, EstadoReserva.CANCELADA)).thenReturn(5);
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(cliente));
        when(reservaDAO.save(any())).thenReturn(reserva);

        ReservaResponseDTO result = reservaService.crear(dto);

        assertNotNull(result);
        assertEquals(Turno.COMIDA, result.getTurno());
        assertEquals("Carlos", result.getNombreCliente());
    }

    @Test
    void crear_turnoCena_exitoso() {
        ReservaRequestDTO dto = buildDTO(Turno.CENA);

        Reserva reservaCena = new Reserva();
        reservaCena.setId(2L);
        reservaCena.setFecha(LocalDate.now());
        reservaCena.setTurno(Turno.CENA);
        reservaCena.setNumComensales(2);
        reservaCena.setEstado(EstadoReserva.CONFIRMADA);
        reservaCena.setCliente(cliente);
        reservaCena.setRestaurante(restaurante);

        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(reservaDAO.countByRestauranteIdAndFechaAndTurnoAndEstadoNot(
                1L, dto.getFecha(), Turno.CENA, EstadoReserva.CANCELADA)).thenReturn(3);
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(cliente));
        when(reservaDAO.save(any())).thenReturn(reservaCena);

        ReservaResponseDTO result = reservaService.crear(dto);

        assertEquals(Turno.CENA, result.getTurno());
    }

    @Test
    void crear_aforoComidaLleno_lanzaExcepcion() {
        ReservaRequestDTO dto = buildDTO(Turno.COMIDA);

        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(reservaDAO.countByRestauranteIdAndFechaAndTurnoAndEstadoNot(
                1L, dto.getFecha(), Turno.COMIDA, EstadoReserva.CANCELADA)).thenReturn(20);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservaService.crear(dto));
        assertTrue(ex.getMessage().contains("Aforo máximo alcanzado"));
        verify(reservaDAO, never()).save(any());
    }

    @Test
    void crear_aforoCenaLleno_lanzaExcepcion() {
        ReservaRequestDTO dto = buildDTO(Turno.CENA);

        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(reservaDAO.countByRestauranteIdAndFechaAndTurnoAndEstadoNot(
                1L, dto.getFecha(), Turno.CENA, EstadoReserva.CANCELADA)).thenReturn(15);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservaService.crear(dto));
        assertTrue(ex.getMessage().contains("CENA"));
    }

    @Test
    void crear_restauranteNoEncontrado_lanzaExcepcion() {
        ReservaRequestDTO dto = buildDTO(Turno.COMIDA);
        when(restauranteDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservaService.crear(dto));
    }

    @Test
    void crear_clienteNoEncontrado_lanzaExcepcion() {
        ReservaRequestDTO dto = buildDTO(Turno.COMIDA);

        when(restauranteDAO.findById(1L)).thenReturn(Optional.of(restaurante));
        when(reservaDAO.countByRestauranteIdAndFechaAndTurnoAndEstadoNot(any(), any(), any(), any())).thenReturn(0);
        when(usuarioDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservaService.crear(dto));
    }

    // ── CANCELAR ───────────────────────────────────────────────────────────

    @Test
    void cancelar_exitoso() {
        when(reservaDAO.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaDAO.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponseDTO result = reservaService.cancelar(1L);

        assertEquals(EstadoReserva.CANCELADA, result.getEstado());
    }

    @Test
    void cancelar_noEncontrada_lanzaExcepcion() {
        when(reservaDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservaService.cancelar(99L));
    }

    // ── CAMBIAR ESTADO ─────────────────────────────────────────────────────

    @Test
    void cambiarEstado_aCompletada() {
        when(reservaDAO.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaDAO.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponseDTO result = reservaService.cambiarEstado(1L, EstadoReserva.COMPLETADA);

        assertEquals(EstadoReserva.COMPLETADA, result.getEstado());
    }

    @Test
    void cambiarEstado_aNoShow() {
        when(reservaDAO.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaDAO.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponseDTO result = reservaService.cambiarEstado(1L, EstadoReserva.NO_SHOW);

        assertEquals(EstadoReserva.NO_SHOW, result.getEstado());
    }

    // ── ASIGNAR CAMARERO ───────────────────────────────────────────────────

    @Test
    void asignarCamarero_exitoso() {
        reserva.setCamarero(camarero);
        when(reservaDAO.findById(1L)).thenReturn(Optional.of(reserva));
        when(usuarioDAO.findById(2L)).thenReturn(Optional.of(camarero));
        when(reservaDAO.save(any())).thenReturn(reserva);

        ReservaResponseDTO result = reservaService.asignarCamarero(1L, 2L);

        assertEquals("Pedro", result.getNombreCamarero());
    }

    // ── CONSULTAS ──────────────────────────────────────────────────────────

    @Test
    void obtenerPorRestaurante_devuelveLista() {
        when(reservaDAO.findByRestauranteId(1L)).thenReturn(List.of(reserva));

        List<ReservaResponseDTO> result = reservaService.obtenerPorRestaurante(1L);

        assertEquals(1, result.size());
    }

    @Test
    void obtenerPorCliente_devuelveLista() {
        when(reservaDAO.findByClienteId(1L)).thenReturn(List.of(reserva));

        List<ReservaResponseDTO> result = reservaService.obtenerPorCliente(1L);

        assertEquals(1, result.size());
        assertEquals("Carlos", result.get(0).getNombreCliente());
    }

    @Test
    void obtenerPorRestauranteYTurno_filtraPorTurno() {
        when(reservaDAO.findByRestauranteIdAndFechaAndTurno(1L, LocalDate.now(), Turno.COMIDA))
                .thenReturn(List.of(reserva));

        List<ReservaResponseDTO> result = reservaService.obtenerPorRestauranteYTurno(1L, LocalDate.now(), Turno.COMIDA);

        assertEquals(1, result.size());
        assertEquals(Turno.COMIDA, result.get(0).getTurno());
    }

    // ── HELPER ─────────────────────────────────────────────────────────────

    private ReservaRequestDTO buildDTO(Turno turno) {
        ReservaRequestDTO dto = new ReservaRequestDTO();
        dto.setFecha(LocalDate.now());
        dto.setTurno(turno);
        dto.setNumComensales(2);
        dto.setClienteId(1L);
        dto.setRestauranteId(1L);
        return dto;
    }
}
