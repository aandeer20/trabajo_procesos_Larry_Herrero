package com.deustoRestaurant.DRestaurant.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MensajeResponseDTO {
    private Long id;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private boolean leido;
    private String nombreRemitente;
    private String nombreDestinatario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
    public String getNombreRemitente() { return nombreRemitente; }
    public void setNombreRemitente(String nombreRemitente) { this.nombreRemitente = nombreRemitente; }
    public String getNombreDestinatario() { return nombreDestinatario; }
    public void setNombreDestinatario(String nombreDestinatario) { this.nombreDestinatario = nombreDestinatario; }
}
