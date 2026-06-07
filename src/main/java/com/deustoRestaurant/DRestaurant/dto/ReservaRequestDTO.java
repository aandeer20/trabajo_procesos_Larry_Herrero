package com.deustoRestaurant.DRestaurant.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaRequestDTO {
    private LocalDate fecha;
    private LocalTime hora;
    private int numComensales;
    private Long clienteId;
    private Long restauranteId;
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public LocalTime getHora() {
		return hora;
	}
	public void setHora(LocalTime hora) {
		this.hora = hora;
	}
	public int getNumComensales() {
		return numComensales;
	}
	public void setNumComensales(int numComensales) {
		this.numComensales = numComensales;
	}
	public Long getClienteId() {
		return clienteId;
	}
	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
	}
	public Long getRestauranteId() {
		return restauranteId;
	}
	public void setRestauranteId(Long restauranteId) {
		this.restauranteId = restauranteId;
	}
}