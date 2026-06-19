package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Notificacion}.
 * Proporciona consultas ordenadas por fecha y filtradas por estado de lectura.
 */

public interface NotificacionDAO extends JpaRepository<Notificacion, Long> {
    /** Devuelve todas las notificaciones de un usuario ordenadas de más reciente a más antigua. */
    List<Notificacion> findByDestinatarioIdOrderByFechaCreacionDesc(Long destinatarioId);
    /** Devuelve las notificaciones de un usuario filtradas por estado de lectura. */
    List<Notificacion> findByDestinatarioIdAndLeida(Long destinatarioId, boolean leida);
    /** Cuenta las notificaciones de un usuario según su estado de lectura. */
    long countByDestinatarioIdAndLeida(Long destinatarioId, boolean leida);
}
