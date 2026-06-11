package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.NotificacionDAO;
import com.deustoRestaurant.DRestaurant.dto.NotificacionResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Notificacion;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de notificaciones internas.
 * Permite crear, consultar y marcar como leídas las notificaciones
 * dirigidas a clientes y gerentes.
 */
@Service
public class NotificacionService {

    @Autowired
    private NotificacionDAO notificacionDAO;

    /**
     * Crea y persiste una nueva notificación para el usuario indicado.
     *
     * @param destinatario usuario que recibirá la notificación
     * @param mensaje      texto descriptivo del evento ocurrido
     */
    public void crear(Usuario destinatario, String mensaje) {
        Notificacion n = new Notificacion();
        n.setDestinatario(destinatario);
        n.setMensaje(mensaje);
        n.setLeida(false);
        n.setFechaCreacion(LocalDateTime.now());
        notificacionDAO.save(n);
    }

    /**
     * Devuelve todas las notificaciones de un usuario ordenadas de más reciente a más antigua.
     *
     * @param usuarioId identificador del usuario destinatario
     * @return lista de notificaciones del usuario
     */
    public List<NotificacionResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return notificacionDAO.findByDestinatarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Cuenta las notificaciones no leídas de un usuario.
     *
     * @param usuarioId identificador del usuario
     * @return número de notificaciones pendientes de lectura
     */
    public long contarNoLeidas(Long usuarioId) {
        return notificacionDAO.countByDestinatarioIdAndLeida(usuarioId, false);
    }

    /**
     * Marca una notificación individual como leída.
     * Si la notificación no existe, no realiza ninguna acción.
     *
     * @param id identificador de la notificación
     */
    public void marcarLeida(Long id) {
        notificacionDAO.findById(id).ifPresent(n -> {
            n.setLeida(true);
            notificacionDAO.save(n);
        });
    }

    /**
     * Marca como leídas todas las notificaciones no leídas de un usuario.
     *
     * @param usuarioId identificador del usuario
     */
    public void marcarTodasLeidas(Long usuarioId) {
        List<Notificacion> noLeidas = notificacionDAO.findByDestinatarioIdAndLeida(usuarioId, false);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionDAO.saveAll(noLeidas);
    }

    /**
     * Convierte una entidad {@link Notificacion} en su DTO de respuesta.
     *
     * @param n entidad a convertir
     * @return DTO con los datos de la notificación
     */
    private NotificacionResponseDTO toDTO(Notificacion n) {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setId(n.getId());
        dto.setMensaje(n.getMensaje());
        dto.setLeida(n.isLeida());
        dto.setFechaCreacion(n.getFechaCreacion());
        return dto;
    }
}
