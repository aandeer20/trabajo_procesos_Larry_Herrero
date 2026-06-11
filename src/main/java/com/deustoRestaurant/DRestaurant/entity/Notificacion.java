package com.deustoRestaurant.DRestaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación del sistema dirigida a un usuario.
 * Se genera automáticamente ante eventos como nueva reserva,
 * cancelación o cambio de estado.
 */

@Entity
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private Usuario destinatario;

    private String mensaje;

    private boolean leida = false;

    private LocalDateTime fechaCreacion;
}
