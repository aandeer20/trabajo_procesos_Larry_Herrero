package com.deustoRestaurant.DRestaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Entidad que representa un restaurante del sistema.
 * Contiene información de contacto, horarios, aforos por turno
 * y las relaciones con su gerente, camareros y reservas.
 */

@Entity
@Data
@Table(name = "restaurantes")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
    private String telefono;
    private String horarioComida;
    private String horarioCena;
    private int aforoMaximoComida;
    private int aforoMaximoCena;
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "gerente_id")
    private Usuario gerente;

    @OneToMany(mappedBy = "restaurante")
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "restaurante")
    private List<Usuario> camareros;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getHorarioComida() {
        return horarioComida;
    }

    public void setHorarioComida(String horarioComida) {
        this.horarioComida = horarioComida;
    }

    public String getHorarioCena() {
        return horarioCena;
    }

    public void setHorarioCena(String horarioCena) {
        this.horarioCena = horarioCena;
    }

    public int getAforoMaximoComida() {
        return aforoMaximoComida;
    }

    public void setAforoMaximoComida(int aforoMaximoComida) {
        this.aforoMaximoComida = aforoMaximoComida;
    }

    public int getAforoMaximoCena() {
        return aforoMaximoCena;
    }

    public void setAforoMaximoCena(int aforoMaximoCena) {
        this.aforoMaximoCena = aforoMaximoCena;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Usuario getGerente() {
        return gerente;
    }

    public void setGerente(Usuario gerente) {
        this.gerente = gerente;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public List<Usuario> getCamareros() {
        return camareros;
    }

    public void setCamareros(List<Usuario> camareros) {
        this.camareros = camareros;
    }
}
