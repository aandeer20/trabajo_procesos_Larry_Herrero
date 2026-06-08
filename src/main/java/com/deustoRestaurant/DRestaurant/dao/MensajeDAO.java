package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeDAO extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByDestinatarioId(Long destinatarioId);
    List<Mensaje> findByRemitenteId(Long remitenteId);
    List<Mensaje> findByDestinatarioIdAndLeido(Long destinatarioId, boolean leido);
    List<Mensaje> findByRemitenteIdAndDestinatarioId(Long remitenteId, Long destinatarioId);
}
