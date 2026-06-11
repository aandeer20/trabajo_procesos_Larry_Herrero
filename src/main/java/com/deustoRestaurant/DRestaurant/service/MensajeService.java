package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.MensajeDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.MensajeRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.MensajeResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Mensaje;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de mensajería interna entre usuarios.
 * Gestiona el envío, la consulta y el marcado de lectura de {@link Mensaje}.
 */
@Service
public class MensajeService {

    @Autowired
    private MensajeDAO mensajeDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    /**
     * Envía un mensaje de un usuario a otro.
     *
     * @param dto datos del mensaje (contenido, remitente, destinatario)
     * @return el mensaje creado como {@link MensajeResponseDTO}
     * @throws RuntimeException si el remitente o el destinatario no existen
     */
    public MensajeResponseDTO enviar(MensajeRequestDTO dto) {
        Usuario remitente = usuarioDAO.findById(dto.getRemitenteId())
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        Usuario destinatario = usuarioDAO.findById(dto.getDestinatarioId())
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(dto.getContenido());
        mensaje.setFechaCreacion(LocalDateTime.now());
        mensaje.setLeido(false);
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);

        return toDTO(mensajeDAO.save(mensaje));
    }

    /**
     * Devuelve todos los mensajes recibidos por un usuario.
     *
     * @param usuarioId identificador del destinatario
     * @return lista de mensajes recibidos
     */
    public List<MensajeResponseDTO> obtenerRecibidos(Long usuarioId) {
        return mensajeDAO.findByDestinatarioId(usuarioId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve los mensajes no leídos de un usuario.
     *
     * @param usuarioId identificador del destinatario
     * @return lista de mensajes pendientes de leer
     */
    public List<MensajeResponseDTO> obtenerNoLeidos(Long usuarioId) {
        return mensajeDAO.findByDestinatarioIdAndLeido(usuarioId, false)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve la conversación entre dos usuarios (mensajes del remitente al destinatario).
     *
     * @param remitenteId    identificador del remitente
     * @param destinatarioId identificador del destinatario
     * @return lista de mensajes entre ambos usuarios en esa dirección
     */
    public List<MensajeResponseDTO> obtenerConversacion(Long remitenteId, Long destinatarioId) {
        return mensajeDAO.findByRemitenteIdAndDestinatarioId(remitenteId, destinatarioId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Marca un mensaje como leído.
     *
     * @param id identificador del mensaje
     * @return el mensaje actualizado con {@code leido = true}
     * @throws RuntimeException si el mensaje no existe
     */
    public MensajeResponseDTO marcarLeido(Long id) {
        Mensaje mensaje = mensajeDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        mensaje.setLeido(true);
        return toDTO(mensajeDAO.save(mensaje));
    }

    /**
     * Convierte una entidad {@link Mensaje} en su DTO de respuesta.
     *
     * @param m entidad a convertir
     * @return DTO con los datos del mensaje
     */
    private MensajeResponseDTO toDTO(Mensaje m) {
        MensajeResponseDTO dto = new MensajeResponseDTO();
        dto.setId(m.getId());
        dto.setContenido(m.getContenido());
        dto.setFechaCreacion(m.getFechaCreacion());
        dto.setLeido(m.isLeido());
        dto.setNombreRemitente(m.getRemitente().getNombre());
        dto.setNombreDestinatario(m.getDestinatario().getNombre());
        return dto;
    }
}
