package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.ReservaDAO;
import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.ReservaRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.ReservaResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;
import com.deustoRestaurant.DRestaurant.entity.Reserva;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Turno;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de reservas.
 * Controla la creación, cancelación, cambio de estado y consulta de {@link Reserva},
 * disparando notificaciones automáticas a clientes y gerentes según corresponda.
 */
@Service
public class ReservaService {

    @Autowired
    private ReservaDAO reservaDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RestauranteDAO restauranteDAO;

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Crea una nueva reserva comprobando el aforo disponible del turno.
     * Notifica a los gerentes del restaurante; si el aforo queda lleno
     * tras la reserva, envía una notificación adicional de aforo completo.
     *
     * @param dto datos de la reserva a crear
     * @return la reserva creada como {@link ReservaResponseDTO}
     * @throws RuntimeException si el restaurante o el cliente no existen,
     *                          o si el aforo máximo ha sido alcanzado
     */
    public ReservaResponseDTO crear(ReservaRequestDTO dto) {
        Restaurante restaurante = restauranteDAO.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

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
        Reserva guardada = reservaDAO.save(reserva);

        String msgNueva = String.format("Nueva solicitud de reserva de %s para el %s turno %s en %s",
                cliente.getNombre(), dto.getFecha(), dto.getTurno(), restaurante.getNombre());
        usuarioDAO.findByRestauranteIdAndRol(restaurante.getId(), Rol.GERENTE)
                .forEach(g -> notificacionService.crear(g, msgNueva));

        int totalTras = reservaDAO.countByRestauranteIdAndFechaAndTurnoAndEstadoNot(
                dto.getRestauranteId(), dto.getFecha(), dto.getTurno(), EstadoReserva.CANCELADA);
        if (totalTras >= aforoMaximo) {
            String msgAforo = String.format("Aforo completo en %s para el %s turno %s",
                    restaurante.getNombre(), dto.getFecha(), dto.getTurno());
            usuarioDAO.findByRestauranteIdAndRol(restaurante.getId(), Rol.GERENTE)
                    .forEach(g -> notificacionService.crear(g, msgAforo));
        }

        return toDTO(guardada);
    }

    /**
     * Cancela una reserva existente y notifica a los gerentes del restaurante.
     *
     * @param id identificador de la reserva
     * @return la reserva con estado {@link EstadoReserva#CANCELADA}
     * @throws RuntimeException si la reserva no existe
     */
    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = reservaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado(EstadoReserva.CANCELADA);
        ReservaResponseDTO resultado = toDTO(reservaDAO.save(reserva));

        String msg = String.format("La reserva de %s para el %s turno %s en %s ha sido cancelada",
                reserva.getCliente().getNombre(), reserva.getFecha(), reserva.getTurno(),
                reserva.getRestaurante().getNombre());
        usuarioDAO.findByRestauranteIdAndRol(reserva.getRestaurante().getId(), Rol.GERENTE)
                .forEach(g -> notificacionService.crear(g, msg));

        return resultado;
    }

    /**
     * Cambia el estado de una reserva y notifica al cliente sobre el cambio.
     *
     * @param id          identificador de la reserva
     * @param nuevoEstado estado al que se desea transicionar
     * @return la reserva actualizada
     * @throws RuntimeException si la reserva no existe
     */
    public ReservaResponseDTO cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado(nuevoEstado);
        ReservaResponseDTO resultado = toDTO(reservaDAO.save(reserva));

        String msg = String.format("Tu reserva del %s turno %s en %s ha cambiado a estado: %s",
                reserva.getFecha(), reserva.getTurno(), reserva.getRestaurante().getNombre(), nuevoEstado);
        notificacionService.crear(reserva.getCliente(), msg);

        return resultado;
    }

    /**
     * Asigna un camarero a una reserva existente.
     *
     * @param reservaId  identificador de la reserva
     * @param camareroId identificador del camarero
     * @return la reserva actualizada con el camarero asignado
     * @throws RuntimeException si la reserva o el camarero no existen
     */
    public ReservaResponseDTO asignarCamarero(Long reservaId, Long camareroId) {
        Reserva reserva = reservaDAO.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        Usuario camarero = usuarioDAO.findById(camareroId)
                .orElseThrow(() -> new RuntimeException("Camarero no encontrado"));
        reserva.setCamarero(camarero);
        return toDTO(reservaDAO.save(reserva));
    }

    /**
     * Devuelve todas las reservas de un restaurante.
     *
     * @param restauranteId identificador del restaurante
     * @return lista de reservas del restaurante
     */
    public List<ReservaResponseDTO> obtenerPorRestaurante(Long restauranteId) {
        return reservaDAO.findByRestauranteId(restauranteId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve todas las reservas de un cliente.
     *
     * @param clienteId identificador del cliente
     * @return lista de reservas del cliente
     */
    public List<ReservaResponseDTO> obtenerPorCliente(Long clienteId) {
        return reservaDAO.findByClienteId(clienteId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve las reservas asignadas a un camarero en una fecha concreta.
     *
     * @param camareroId identificador del camarero
     * @param fecha      fecha de las reservas
     * @return lista de reservas del camarero ese día
     */
    public List<ReservaResponseDTO> obtenerPorCamareroYFecha(Long camareroId, LocalDate fecha) {
        return reservaDAO.findByCamareroIdAndFecha(camareroId, fecha)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve las reservas de un restaurante filtradas por fecha y turno.
     *
     * @param restauranteId identificador del restaurante
     * @param fecha         fecha de las reservas
     * @param turno         turno ({@link Turno#COMIDA} o {@link Turno#CENA})
     * @return lista de reservas que coinciden con los criterios
     */
    public List<ReservaResponseDTO> obtenerPorRestauranteYTurno(Long restauranteId, LocalDate fecha, Turno turno) {
        return reservaDAO.findByRestauranteIdAndFechaAndTurno(restauranteId, fecha, turno)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve todas las reservas en estado {@link EstadoReserva#PENDIENTE}.
     *
     * @return lista de reservas pendientes de confirmación
     */
    public List<ReservaResponseDTO> obtenerPendientes() {
        return reservaDAO.findByEstado(EstadoReserva.PENDIENTE)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve las reservas pendientes de un restaurante concreto.
     *
     * @param restauranteId identificador del restaurante
     * @return lista de reservas pendientes del restaurante
     */
    public List<ReservaResponseDTO> obtenerPendientesPorRestaurante(Long restauranteId) {
        return reservaDAO.findByRestauranteIdAndEstado(restauranteId, EstadoReserva.PENDIENTE)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Convierte una entidad {@link Reserva} en su DTO de respuesta.
     *
     * @param r entidad a convertir
     * @return DTO con los datos de la reserva
     */
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
