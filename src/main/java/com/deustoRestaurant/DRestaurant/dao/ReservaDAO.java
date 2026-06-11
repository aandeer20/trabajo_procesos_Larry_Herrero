package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;
import com.deustoRestaurant.DRestaurant.entity.Reserva;
import com.deustoRestaurant.DRestaurant.entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Reserva}.
 * Proporciona consultas por cliente, restaurante, camarero, fecha, turno y estado.
 */

public interface ReservaDAO extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByRestauranteId(Long restauranteId);
    List<Reserva> findByCamareroId(Long camareroId);
    List<Reserva> findByCamareroIdAndFecha(Long camareroId, LocalDate fecha);
    List<Reserva> findByRestauranteIdAndFechaAndTurno(Long restauranteId, LocalDate fecha, Turno turno);
    int countByRestauranteIdAndFechaAndTurnoAndEstadoNot(Long restauranteId, LocalDate fecha, Turno turno, EstadoReserva estado);
    List<Reserva> findByEstado(EstadoReserva estado);
    List<Reserva> findByRestauranteIdAndEstado(Long restauranteId, EstadoReserva estado);
}
