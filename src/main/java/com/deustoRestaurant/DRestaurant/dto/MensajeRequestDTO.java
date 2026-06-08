package com.deustoRestaurant.DRestaurant.dto;

import lombok.Data;

@Data
public class MensajeRequestDTO {
    private String contenido;
    private Long remitenteId;
    private Long destinatarioId;

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public Long getRemitenteId() { return remitenteId; }
    public void setRemitenteId(Long remitenteId) { this.remitenteId = remitenteId; }
    public Long getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(Long destinatarioId) { this.destinatarioId = destinatarioId; }
}
