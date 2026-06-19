package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.NotificacionResponseDTO;
import com.deustoRestaurant.DRestaurant.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de notificaciones internas.
 * Permite consultar notificaciones, contar las no leídas
 * y marcarlas como leídas de forma individual o masiva.
 * Base URL: {@code /api/notificaciones}
 */

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionFacade {

    @Autowired
    private NotificacionService notificacionService;

    /**
     * Devuelve todas las notificaciones de un usuario ordenadas por fecha descendente.
     *
     * @param usuarioId identificador del usuario
     * @return lista de notificaciones del usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public List<NotificacionResponseDTO> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return notificacionService.obtenerPorUsuario(usuarioId);
    }

    /**
     * Devuelve el número de notificaciones no leídas de un usuario.
     *
     * @param usuarioId identificador del usuario
     * @return mapa con la clave {@code total} y el número de notificaciones pendientes
     */
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public Map<String, Long> contarNoLeidas(@PathVariable Long usuarioId) {
        return Map.of("total", notificacionService.contarNoLeidas(usuarioId));
    }

    /**
     * Marca una notificación individual como leída.
     *
     * @param id identificador de la notificación
     */
    @PutMapping("/{id}/leer")
    public void marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
    }

    /**
     * Marca como leídas todas las notificaciones no leídas de un usuario.
     *
     * @param usuarioId identificador del usuario
     */
    @PutMapping("/usuario/{usuarioId}/leer-todas")
    public void marcarTodasLeidas(@PathVariable Long usuarioId) {
        notificacionService.marcarTodasLeidas(usuarioId);
    }
}
