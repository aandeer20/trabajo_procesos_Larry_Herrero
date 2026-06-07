package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.ReservaRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.ReservaResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.EstadoReserva;
import com.deustoRestaurant.DRestaurant.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaFacade {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ReservaResponseDTO crear(@RequestBody ReservaRequestDTO dto) {
        return reservaService.crear(dto);
    }

    @PutMapping("/{id}/cancelar")
    public ReservaResponseDTO cancelar(@PathVariable Long id) {
        return reservaService.cancelar(id);
    }

    @PutMapping("/{id}/estado")
    public ReservaResponseDTO cambiarEstado(@PathVariable Long id, @RequestParam EstadoReserva estado) {
        return reservaService.cambiarEstado(id, estado);
    }

    @GetMapping("/restaurante/{id}")
    public List<ReservaResponseDTO> obtenerPorRestaurante(@PathVariable Long id) {
        return reservaService.obtenerPorRestaurante(id);
    }

    @GetMapping("/cliente/{id}")
    public List<ReservaResponseDTO> obtenerPorCliente(@PathVariable Long id) {
        return reservaService.obtenerPorCliente(id);
    }

    @GetMapping("/turno")
    public List<ReservaResponseDTO> obtenerPorCamareroYFecha(
            @RequestParam Long camareroId,
            @RequestParam LocalDate fecha) {
        return reservaService.obtenerPorCamareroYFecha(camareroId, fecha);
    }
}