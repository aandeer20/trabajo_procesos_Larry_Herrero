package com.deustoRestaurant.DRestaurant.dto;

import lombok.Data;

@Data
public class RestauranteResponseDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String horarioComida;
    private String horarioCena;
    private int aforoMaximoComida;
    private int aforoMaximoCena;
    private boolean activo;
    private String nombreGerente;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getNombreGerente() { return nombreGerente; }
    public void setNombreGerente(String nombreGerente) { this.nombreGerente = nombreGerente; }
}
