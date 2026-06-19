package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Mensaje}.
 * Proporciona consultas por destinatario, remitente y estado de lectura.
 */

public interface MensajeDAO extends JpaRepository<Mensaje, Long> {
    /** Devuelve todos los mensajes recibidos por el usuario indicado. */
    List<Mensaje> findByDestinatarioId(Long destinatarioId);
    /** Devuelve todos los mensajes enviados por el usuario indicado. */
    List<Mensaje> findByRemitenteId(Long remitenteId);
    /** Devuelve los mensajes del destinatario filtrados por estado de lectura. */
    List<Mensaje> findByDestinatarioIdAndLeido(Long destinatarioId, boolean leido);
    /** Devuelve los mensajes de una conversación entre remitente y destinatario. */
    List<Mensaje> findByRemitenteIdAndDestinatarioId(Long remitenteId, Long destinatarioId);
}
