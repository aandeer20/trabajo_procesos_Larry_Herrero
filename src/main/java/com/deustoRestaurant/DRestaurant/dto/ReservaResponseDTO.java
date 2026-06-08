package com.deustoRestaurant.DRestaurant.dto;

import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;
import com.deustoRestaurant.DRestaurant.entity.Turno;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservaResponseDTO {
    private Long id;
    private LocalDate fecha;
    private Turno turno;
    private int numComensales;
    private String observaciones;
    private EstadoReserva estado;
    private String nombreCliente;
    private String nombreRestaurante;
    private String nombreCamarero;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public int getNumComensales() { return numComensales; }
    public void setNumComensales(int numComensales) { this.numComensales = numComensales; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getNombreRestaurante() { return nombreRestaurante; }
    public void setNombreRestaurante(String nombreRestaurante) { this.nombreRestaurante = nombreRestaurante; }
    public String getNombreCamarero() { return nombreCamarero; }
    public void setNombreCamarero(String nombreCamarero) { this.nombreCamarero = nombreCamarero; }
}
