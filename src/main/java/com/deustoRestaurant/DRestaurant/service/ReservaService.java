package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.ReservaDAO;
import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.ReservaRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.ReservaResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;
import com.deustoRestaurant.DRestaurant.entity.Reserva;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
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

        // Issue #6: comprobar aforo antes de crear la reserva
        int reservasActuales = reservaDAO.countByRestauranteIdAndFechaAndHoraAndEstadoNot(
                dto.getRestauranteId(), dto.getFecha(), dto.getHora(), EstadoReserva.CANCELADA);
        if (reservasActuales >= restaurante.getAforoMaximo()) {
            throw new RuntimeException("Aforo máximo alcanzado para esa franja horaria");
        }

        Usuario cliente = usuarioDAO.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setFecha(dto.getFecha());
        reserva.setHora(dto.getHora());
        reserva.setNumComensales(dto.getNumComensales());
        reserva.setCliente(cliente);
        reserva.setRestaurante(restaurante);
        reserva.setEstado(EstadoReserva.CONFIRMADA);

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

    private ReservaResponseDTO toDTO(Reserva r) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(r.getId());
        dto.setFecha(r.getFecha());
        dto.setHora(r.getHora());
        dto.setNumComensales(r.getNumComensales());
        dto.setEstado(r.getEstado());
        dto.setNombreCliente(r.getCliente().getNombre());
        dto.setNombreRestaurante(r.getRestaurante().getNombre());
        if (r.getCamarero() != null) {
            dto.setNombreCamarero(r.getCamarero().getNombre());
        }
        return dto;
    }
}