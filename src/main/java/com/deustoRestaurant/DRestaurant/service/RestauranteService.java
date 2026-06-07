package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteDAO restauranteDAO;

    public RestauranteResponseDTO crear(RestauranteRequestDTO dto) {
        Restaurante r = new Restaurante();
        r.setNombre(dto.getNombre());
        r.setDireccion(dto.getDireccion());
        r.setHorario(dto.getHorario());
        r.setAforoMaximo(dto.getAforoMaximo());
        return toDTO(restauranteDAO.save(r));
    }

    public RestauranteResponseDTO actualizar(Long id, RestauranteRequestDTO dto) {
        Restaurante r = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        r.setNombre(dto.getNombre());
        r.setDireccion(dto.getDireccion());
        r.setHorario(dto.getHorario());
        r.setAforoMaximo(dto.getAforoMaximo());
        return toDTO(restauranteDAO.save(r));
    }

    public RestauranteResponseDTO actualizarAforo(Long id, int nuevoAforo) {
        Restaurante r = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        r.setAforoMaximo(nuevoAforo);
        return toDTO(restauranteDAO.save(r));
    }

    public List<RestauranteResponseDTO> obtenerTodos() {
        return restauranteDAO.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private RestauranteResponseDTO toDTO(Restaurante r) {
        RestauranteResponseDTO dto = new RestauranteResponseDTO();
        dto.setId(r.getId());
        dto.setNombre(r.getNombre());
        dto.setDireccion(r.getDireccion());
        dto.setHorario(r.getHorario());
        dto.setAforoMaximo(r.getAforoMaximo());
        return dto;
    }
}