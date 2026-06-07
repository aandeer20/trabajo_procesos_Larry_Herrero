package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.RestauranteRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteResponseDTO;
import com.deustoRestaurant.DRestaurant.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteFacade {

    @Autowired
    private RestauranteService restauranteService;

    @PostMapping
    public RestauranteResponseDTO crear(@RequestBody RestauranteRequestDTO dto) {
        return restauranteService.crear(dto);
    }

    @PutMapping("/{id}")
    public RestauranteResponseDTO actualizar(@PathVariable Long id, @RequestBody RestauranteRequestDTO dto) {
        return restauranteService.actualizar(id, dto);
    }

    @PutMapping("/{id}/aforo")
    public RestauranteResponseDTO actualizarAforo(@PathVariable Long id, @RequestParam int aforo) {
        return restauranteService.actualizarAforo(id, aforo);
    }

    @GetMapping
    public List<RestauranteResponseDTO> obtenerTodos() {
        return restauranteService.obtenerTodos();
    }
}