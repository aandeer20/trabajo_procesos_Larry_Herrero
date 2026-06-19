package com.deustoRestaurant.DRestaurant.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con los datos de una notificación interna del sistema.
 */
@Data
public class NotificacionResponseDTO {
    private Long id;
    private String mensaje;
    private boolean leida;
    private LocalDateTime fechaCreacion;
}
