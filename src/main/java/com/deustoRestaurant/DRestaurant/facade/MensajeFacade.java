package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.MensajeRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.MensajeResponseDTO;
import com.deustoRestaurant.DRestaurant.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeFacade {

    @Autowired
    private MensajeService mensajeService;

    @PostMapping
    public MensajeResponseDTO enviar(@RequestBody MensajeRequestDTO dto) {
        return mensajeService.enviar(dto);
    }

    @GetMapping("/recibidos/{destinatarioId}")
    public List<MensajeResponseDTO> obtenerRecibidos(@PathVariable Long destinatarioId) {
        return mensajeService.obtenerRecibidos(destinatarioId);
    }

    @GetMapping("/noleidos/{destinatarioId}")
    public List<MensajeResponseDTO> obtenerNoLeidos(@PathVariable Long destinatarioId) {
        return mensajeService.obtenerNoLeidos(destinatarioId);
    }

    @GetMapping("/conversacion")
    public List<MensajeResponseDTO> obtenerConversacion(
            @RequestParam Long remitenteId,
            @RequestParam Long destinatarioId) {
        return mensajeService.obtenerConversacion(remitenteId, destinatarioId);
    }

    @PutMapping("/{id}/leido")
    public MensajeResponseDTO marcarLeido(@PathVariable Long id) {
        return mensajeService.marcarLeido(id);
    }
}
