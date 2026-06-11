package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.NotificacionResponseDTO;
import com.deustoRestaurant.DRestaurant.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionFacade {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/usuario/{usuarioId}")
    public List<NotificacionResponseDTO> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return notificacionService.obtenerPorUsuario(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public Map<String, Long> contarNoLeidas(@PathVariable Long usuarioId) {
        return Map.of("total", notificacionService.contarNoLeidas(usuarioId));
    }

    @PutMapping("/{id}/leer")
    public void marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
    }

    @PutMapping("/usuario/{usuarioId}/leer-todas")
    public void marcarTodasLeidas(@PathVariable Long usuarioId) {
        notificacionService.marcarTodasLeidas(usuarioId);
    }
}
