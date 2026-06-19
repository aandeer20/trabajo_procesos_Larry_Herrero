package com.deustoRestaurant.DRestaurant.dto;

/**
 * DTO de solicitud para actualizar el aforo máximo de un turno.
 */
public class AforoRequestDTO {
    private int aforo;
    public int getAforo() { return aforo; }
    public void setAforo(int aforo) { this.aforo = aforo; }
}