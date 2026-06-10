package com.deustoRestaurant.DRestaurant.dto;

import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;

public class CambiarEstadoDTO {
    private EstadoReserva estado;
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
}