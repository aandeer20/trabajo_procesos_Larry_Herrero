package com.deustoRestaurant.DRestaurant.dto;

import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;

/**
 * DTO de solicitud para cambiar el estado de una reserva.
 */
public class CambiarEstadoDTO {
    private EstadoReserva estado;
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
}