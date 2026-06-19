package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.MensajeRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.MensajeResponseDTO;
import com.deustoRestaurant.DRestaurant.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para la mensajería interna entre usuarios.
 * Expone endpoints para enviar mensajes, consultar recibidos,
 * obtener no leídos, ver conversaciones y marcar mensajes como leídos.
 * Base URL: {@code /api/mensajes}
 */

@RestController
@RequestMapping("/api/mensajes")
public class MensajeFacade {

    @Autowired
    private MensajeService mensajeService;

    /**
     * Envía un nuevo mensaje interno entre usuarios.
     *
     * @param dto datos del mensaje (contenido, remitente, destinatario)
     * @return el mensaje creado con estado 201, o 404 si algún usuario no existe
     */
    @PostMapping
    public ResponseEntity<MensajeResponseDTO> enviar(@RequestBody MensajeRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(mensajeService.enviar(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Devuelve todos los mensajes recibidos por un usuario.
     *
     * @param destinatarioId identificador del destinatario
     * @return lista de mensajes recibidos
     */
    @GetMapping("/recibidos/{destinatarioId}")
    public List<MensajeResponseDTO> obtenerRecibidos(@PathVariable Long destinatarioId) {
        return mensajeService.obtenerRecibidos(destinatarioId);
    }

    /**
     * Devuelve los mensajes no leídos de un usuario.
     *
     * @param destinatarioId identificador del destinatario
     * @return lista de mensajes pendientes de leer
     */
    @GetMapping("/noleidos/{destinatarioId}")
    public List<MensajeResponseDTO> obtenerNoLeidos(@PathVariable Long destinatarioId) {
        return mensajeService.obtenerNoLeidos(destinatarioId);
    }

    /**
     * Devuelve la conversación entre dos usuarios.
     *
     * @param remitenteId    identificador del remitente
     * @param destinatarioId identificador del destinatario
     * @return lista de mensajes entre ambos usuarios
     */
    @GetMapping("/conversacion")
    public List<MensajeResponseDTO> obtenerConversacion(
            @RequestParam Long remitenteId,
            @RequestParam Long destinatarioId) {
        return mensajeService.obtenerConversacion(remitenteId, destinatarioId);
    }

    /**
     * Marca un mensaje como leído.
     *
     * @param id identificador del mensaje
     * @return el mensaje actualizado
     */
    @PutMapping("/{id}/leido")
    public MensajeResponseDTO marcarLeido(@PathVariable Long id) {
        return mensajeService.marcarLeido(id);
    }
}