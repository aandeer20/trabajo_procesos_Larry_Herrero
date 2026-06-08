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

@Service
public class MensajeService {

    @Autowired
    private MensajeDAO mensajeDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    public MensajeResponseDTO enviar(MensajeRequestDTO dto) {
        Usuario remitente = usuarioDAO.findById(dto.getRemitenteId())
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        Usuario destinatario = usuarioDAO.findById(dto.getDestinatarioId())
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(dto.getContenido());
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setFechaCreacion(LocalDateTime.now());

        return toDTO(mensajeDAO.save(mensaje));
    }

    public List<MensajeResponseDTO> obtenerRecibidos(Long destinatarioId) {
        return mensajeDAO.findByDestinatarioId(destinatarioId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MensajeResponseDTO> obtenerNoLeidos(Long destinatarioId) {
        return mensajeDAO.findByDestinatarioIdAndLeido(destinatarioId, false)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MensajeResponseDTO> obtenerConversacion(Long remitenteId, Long destinatarioId) {
        return mensajeDAO.findByRemitenteIdAndDestinatarioId(remitenteId, destinatarioId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MensajeResponseDTO marcarLeido(Long id) {
        Mensaje mensaje = mensajeDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        mensaje.setLeido(true);
        return toDTO(mensajeDAO.save(mensaje));
    }

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
