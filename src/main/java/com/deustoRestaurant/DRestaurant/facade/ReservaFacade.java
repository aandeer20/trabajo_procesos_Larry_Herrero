package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.CambiarEstadoDTO;
import com.deustoRestaurant.DRestaurant.dto.ReservaRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.ReservaResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Turno;
import com.deustoRestaurant.DRestaurant.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la gestión de reservas.
 * Expone endpoints para crear, cancelar, cambiar estado, asignar camarero
 * y consultar reservas, incluidas las pendientes de confirmación.
 */
@RestController
@RequestMapping("/api/reservas")
public class ReservaFacade {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@RequestBody ReservaRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}/cancelar")
    public ReservaResponseDTO cancelar(@PathVariable Long id) {
        return reservaService.cancelar(id);
    }

    @PutMapping("/{id}/estado")
    public ReservaResponseDTO cambiarEstado(@PathVariable Long id, @RequestBody CambiarEstadoDTO dto) {
        return reservaService.cambiarEstado(id, dto.getEstado());
    }

    @PutMapping("/{reservaId}/camarero/{camareroId}")
    public ReservaResponseDTO asignarCamarero(@PathVariable Long reservaId, @PathVariable Long camareroId) {
        return reservaService.asignarCamarero(reservaId, camareroId);
    }

    @GetMapping("/restaurante/{id}")
    public List<ReservaResponseDTO> obtenerPorRestaurante(@PathVariable Long id) {
        return reservaService.obtenerPorRestaurante(id);
    }

    @GetMapping("/cliente/{id}")
    public List<ReservaResponseDTO> obtenerPorCliente(@PathVariable Long id) {
        return reservaService.obtenerPorCliente(id);
    }

    @GetMapping("/camarero")
    public List<ReservaResponseDTO> obtenerPorCamareroYFecha(
            @RequestParam Long camareroId,
            @RequestParam LocalDate fecha) {
        return reservaService.obtenerPorCamareroYFecha(camareroId, fecha);
    }

    @GetMapping("/turno")
    public List<ReservaResponseDTO> obtenerPorRestauranteYTurno(
            @RequestParam Long restauranteId,
            @RequestParam LocalDate fecha,
            @RequestParam Turno turno) {
        return reservaService.obtenerPorRestauranteYTurno(restauranteId, fecha, turno);
    }

    @GetMapping("/pendientes")
    public List<ReservaResponseDTO> obtenerPendientes() {
        return reservaService.obtenerPendientes();
    }

    @GetMapping("/pendientes/restaurante/{restauranteId}")
    public List<ReservaResponseDTO> obtenerPendientesPorRestaurante(@PathVariable Long restauranteId) {
        return reservaService.obtenerPendientesPorRestaurante(restauranteId);
    }
}
