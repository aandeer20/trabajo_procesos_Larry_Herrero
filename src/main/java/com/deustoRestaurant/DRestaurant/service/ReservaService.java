package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.ReservaDAO;
import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.ReservaRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.ReservaResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;
import com.deustoRestaurant.DRestaurant.entity.Reserva;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import com.deustoRestaurant.DRestaurant.entity.Turno;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaDAO reservaDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RestauranteDAO restauranteDAO;

    public ReservaResponseDTO crear(ReservaRequestDTO dto) {
        Restaurante restaurante = restauranteDAO.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        // Comprobar aforo del turno correspondiente
        int aforoMaximo = dto.getTurno() == Turno.COMIDA
                ? restaurante.getAforoMaximoComida()
                : restaurante.getAforoMaximoCena();

        int reservasActuales = reservaDAO.countByRestauranteIdAndFechaAndTurnoAndEstadoNot(
                dto.getRestauranteId(), dto.getFecha(), dto.getTurno(), EstadoReserva.CANCELADA);

        if (reservasActuales >= aforoMaximo) {
            throw new RuntimeException("Aforo máximo alcanzado para el turno " + dto.getTurno());
        }

        Usuario cliente = usuarioDAO.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setFecha(dto.getFecha());
        reserva.setTurno(dto.getTurno());
        reserva.setNumComensales(dto.getNumComensales());
        reserva.setObservaciones(dto.getObservaciones());
        reserva.setCliente(cliente);
        reserva.setRestaurante(restaurante);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        return toDTO(reservaDAO.save(reserva));
    }

    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = reservaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado(EstadoReserva.CANCELADA);
        return toDTO(reservaDAO.save(reserva));
    }

    public ReservaResponseDTO cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado(nuevoEstado);
        return toDTO(reservaDAO.save(reserva));
    }

    public ReservaResponseDTO asignarCamarero(Long reservaId, Long camareroId) {
        Reserva reserva = reservaDAO.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        Usuario camarero = usuarioDAO.findById(camareroId)
                .orElseThrow(() -> new RuntimeException("Camarero no encontrado"));
        reserva.setCamarero(camarero);
        return toDTO(reservaDAO.save(reserva));
    }

    public List<ReservaResponseDTO> obtenerPorRestaurante(Long restauranteId) {
        return reservaDAO.findByRestauranteId(restauranteId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> obtenerPorCliente(Long clienteId) {
        return reservaDAO.findByClienteId(clienteId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> obtenerPorCamareroYFecha(Long camareroId, LocalDate fecha) {
        return reservaDAO.findByCamareroIdAndFecha(camareroId, fecha)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> obtenerPorRestauranteYTurno(Long restauranteId, LocalDate fecha, Turno turno) {
        return reservaDAO.findByRestauranteIdAndFechaAndTurno(restauranteId, fecha, turno)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ReservaResponseDTO toDTO(Reserva r) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(r.getId());
        dto.setFecha(r.getFecha());
        dto.setTurno(r.getTurno());
        dto.setNumComensales(r.getNumComensales());
        dto.setObservaciones(r.getObservaciones());
        dto.setEstado(r.getEstado());
        dto.setNombreCliente(r.getCliente().getNombre());
        dto.setNombreRestaurante(r.getRestaurante().getNombre());
        if (r.getCamarero() != null) {
            dto.setNombreCamarero(r.getCamarero().getNombre());
        }
        return dto;
    }
}
