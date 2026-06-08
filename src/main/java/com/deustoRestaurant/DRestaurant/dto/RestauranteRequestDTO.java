package com.deustoRestaurant.DRestaurant.dto;

import lombok.Data;

@Data
public class RestauranteRequestDTO {
    private String nombre;
    private String direccion;
    private String telefono;
    private String horarioComida;
    private String horarioCena;
    private int aforoMaximoComida;
    private int aforoMaximoCena;
    private Long gerenteId;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getHorarioComida() { return horarioComida; }
    public void setHorarioComida(String horarioComida) { this.horarioComida = horarioComida; }
    public String getHorarioCena() { return horarioCena; }
    public void setHorarioCena(String horarioCena) { this.horarioCena = horarioCena; }
    public int getAforoMaximoComida() { return aforoMaximoComida; }
    public void setAforoMaximoComida(int aforoMaximoComida) { this.aforoMaximoComida = aforoMaximoComida; }
    public int getAforoMaximoCena() { return aforoMaximoCena; }
    public void setAforoMaximoCena(int aforoMaximoCena) { this.aforoMaximoCena = aforoMaximoCena; }
    public Long getGerenteId() { return gerenteId; }
    public void setGerenteId(Long gerenteId) { this.gerenteId = gerenteId; }
}
