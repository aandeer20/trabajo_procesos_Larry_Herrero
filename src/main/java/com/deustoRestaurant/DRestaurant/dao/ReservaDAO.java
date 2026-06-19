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
    /** Devuelve todas las reservas de un cliente. */
    List<Reserva> findByClienteId(Long clienteId);
    /** Devuelve todas las reservas de un restaurante. */
    List<Reserva> findByRestauranteId(Long restauranteId);
    /** Devuelve todas las reservas asignadas a un camarero. */
    List<Reserva> findByCamareroId(Long camareroId);
    /** Devuelve las reservas de un camarero para una fecha concreta. */
    List<Reserva> findByCamareroIdAndFecha(Long camareroId, LocalDate fecha);
    /** Devuelve las reservas de un restaurante en una fecha y turno concretos. */
    List<Reserva> findByRestauranteIdAndFechaAndTurno(Long restauranteId, LocalDate fecha, Turno turno);
    /** Cuenta las reservas activas (excluye el estado dado) para calcular el aforo ocupado. */
    int countByRestauranteIdAndFechaAndTurnoAndEstadoNot(Long restauranteId, LocalDate fecha, Turno turno, EstadoReserva estado);
    /** Devuelve todas las reservas con un estado determinado. */
    List<Reserva> findByEstado(EstadoReserva estado);
    /** Devuelve las reservas de un restaurante filtradas por estado. */
    List<Reserva> findByRestauranteIdAndEstado(Long restauranteId, EstadoReserva estado);
}
