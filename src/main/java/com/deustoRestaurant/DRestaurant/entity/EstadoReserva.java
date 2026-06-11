package com.deustoRestaurant.DRestaurant.entity;

/**
 * Estados posibles de una reserva a lo largo de su ciclo de vida.
 */
public enum EstadoReserva {
    /** Reserva creada y pendiente de confirmación. */
    PENDIENTE,
    /** Reserva confirmada por el restaurante. */
    CONFIRMADA,
    /** Reserva completada satisfactoriamente. */
    COMPLETADA,
    /** Reserva cancelada por el cliente o el restaurante. */
    CANCELADA,
    /** El cliente no se presentó. */
    NO_SHOW
}