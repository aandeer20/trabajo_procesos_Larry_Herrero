package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Notificacion}.
 * Proporciona consultas ordenadas por fecha y filtradas por estado de lectura.
 */

public interface NotificacionDAO extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByDestinatarioIdOrderByFechaCreacionDesc(Long destinatarioId);
    List<Notificacion> findByDestinatarioIdAndLeida(Long destinatarioId, boolean leida);
    long countByDestinatarioIdAndLeida(Long destinatarioId, boolean leida);
}
