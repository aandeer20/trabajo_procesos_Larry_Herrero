package com.deustoRestaurant.DRestaurant.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionResponseDTO {
    private Long id;
    private String mensaje;
    private boolean leida;
    private LocalDateTime fechaCreacion;
}
