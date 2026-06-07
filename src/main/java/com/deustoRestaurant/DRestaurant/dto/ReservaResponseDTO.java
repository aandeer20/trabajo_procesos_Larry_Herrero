package com.deustoRestaurant.DRestaurant.dto;

import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaResponseDTO {
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private int numComensales;
    private EstadoReserva estado;
    private String nombreCliente;
    private String nombreRestaurante;
    private String nombreCamarero;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public EstadoReserva getEstado() {
		return estado;
	}
	public void setEstado(EstadoReserva estado) {
		this.estado = estado;
	}
	public String getNombreCliente() {
		return nombreCliente;
	}
	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
	public String getNombreRestaurante() {
		return nombreRestaurante;
	}
	public void setNombreRestaurante(String nombreRestaurante) {
		this.nombreRestaurante = nombreRestaurante;
	}
	public String getNombreCamarero() {
		return nombreCamarero;
	}
	public void setNombreCamarero(String nombreCamarero) {
		this.nombreCamarero = nombreCamarero;
	}
}