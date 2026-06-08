package com.deustoRestaurant.DRestaurant.dto;

import com.deustoRestaurant.DRestaurant.entity.Turno;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservaRequestDTO {
    private LocalDate fecha;
    private Turno turno;
    private int numComensales;
    private String observaciones;
    private Long clienteId;
    private Long restauranteId;

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public int getNumComensales() { return numComensales; }
    public void setNumComensales(int numComensales) { this.numComensales = numComensales; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
}
