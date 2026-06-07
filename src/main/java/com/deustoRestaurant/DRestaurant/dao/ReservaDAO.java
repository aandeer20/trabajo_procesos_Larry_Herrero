package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;
import com.deustoRestaurant.DRestaurant.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaDAO extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByRestauranteId(Long restauranteId);
    List<Reserva> findByRestauranteIdAndFechaAndHora(Long restauranteId, LocalDate fecha, LocalTime hora);
    List<Reserva> findByCamareroIdAndFecha(Long camareroId, LocalDate fecha);
    int countByRestauranteIdAndFechaAndHoraAndEstadoNot(Long restauranteId, LocalDate fecha, LocalTime hora, EstadoReserva estado);
}