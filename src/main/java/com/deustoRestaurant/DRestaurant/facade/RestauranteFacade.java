package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.AforoRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteResponseDTO;
import com.deustoRestaurant.DRestaurant.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteFacade {

    @Autowired
    private RestauranteService restauranteService;

    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> crear(@RequestBody RestauranteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteService.crear(dto));
    }

    @PutMapping("/{id}")
    public RestauranteResponseDTO actualizar(@PathVariable Long id, @RequestBody RestauranteRequestDTO dto) {
        return restauranteService.actualizar(id, dto);
    }

    @PutMapping("/{id}/aforo/comida")
    public RestauranteResponseDTO actualizarAforoComida(@PathVariable Long id, @RequestBody AforoRequestDTO dto) {
        return restauranteService.actualizarAforoComida(id, dto.getAforo());
    }

    @PutMapping("/{id}/aforo/cena")
    public RestauranteResponseDTO actualizarAforoCena(@PathVariable Long id, @RequestBody AforoRequestDTO dto) {
        return restauranteService.actualizarAforoCena(id, dto.getAforo());
    }

    @DeleteMapping("/{id}")
    public RestauranteResponseDTO desactivar(@PathVariable Long id) {
        return restauranteService.desactivar(id);
    }

    @GetMapping
    public List<RestauranteResponseDTO> obtenerTodos() {
        return restauranteService.obtenerTodos();
    }

    @GetMapping("/activos")
    public List<RestauranteResponseDTO> obtenerActivos() {
        return restauranteService.obtenerActivos();
    }
}