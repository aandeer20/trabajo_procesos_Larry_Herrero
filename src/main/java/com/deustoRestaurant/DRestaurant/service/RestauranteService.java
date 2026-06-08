package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.RestauranteResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteDAO restauranteDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    public RestauranteResponseDTO crear(RestauranteRequestDTO dto) {
        Restaurante r = new Restaurante();
        r.setNombre(dto.getNombre());
        r.setDireccion(dto.getDireccion());
        r.setTelefono(dto.getTelefono());
        r.setHorarioComida(dto.getHorarioComida());
        r.setHorarioCena(dto.getHorarioCena());
        r.setAforoMaximoComida(dto.getAforoMaximoComida());
        r.setAforoMaximoCena(dto.getAforoMaximoCena());
        if (dto.getGerenteId() != null) {
            Usuario gerente = usuarioDAO.findById(dto.getGerenteId())
                    .orElseThrow(() -> new RuntimeException("Gerente no encontrado"));
            r.setGerente(gerente);
        }
        return toDTO(restauranteDAO.save(r));
    }

    public RestauranteResponseDTO actualizar(Long id, RestauranteRequestDTO dto) {
        Restaurante r = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        r.setNombre(dto.getNombre());
        r.setDireccion(dto.getDireccion());
        r.setTelefono(dto.getTelefono());
        r.setHorarioComida(dto.getHorarioComida());
        r.setHorarioCena(dto.getHorarioCena());
        r.setAforoMaximoComida(dto.getAforoMaximoComida());
        r.setAforoMaximoCena(dto.getAforoMaximoCena());
        return toDTO(restauranteDAO.save(r));
    }

    public RestauranteResponseDTO actualizarAforoComida(Long id, int nuevoAforo) {
        Restaurante r = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        r.setAforoMaximoComida(nuevoAforo);
        return toDTO(restauranteDAO.save(r));
    }

    public RestauranteResponseDTO actualizarAforoCena(Long id, int nuevoAforo) {
        Restaurante r = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        r.setAforoMaximoCena(nuevoAforo);
        return toDTO(restauranteDAO.save(r));
    }

    public RestauranteResponseDTO desactivar(Long id) {
        Restaurante r = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        r.setActivo(false);
        return toDTO(restauranteDAO.save(r));
    }

    public List<RestauranteResponseDTO> obtenerTodos() {
        return restauranteDAO.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<RestauranteResponseDTO> obtenerActivos() {
        return restauranteDAO.findByActivo(true)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private RestauranteResponseDTO toDTO(Restaurante r) {
        RestauranteResponseDTO dto = new RestauranteResponseDTO();
        dto.setId(r.getId());
        dto.setNombre(r.getNombre());
        dto.setDireccion(r.getDireccion());
        dto.setTelefono(r.getTelefono());
        dto.setHorarioComida(r.getHorarioComida());
        dto.setHorarioCena(r.getHorarioCena());
        dto.setAforoMaximoComida(r.getAforoMaximoComida());
        dto.setAforoMaximoCena(r.getAforoMaximoCena());
        dto.setActivo(r.isActivo());
        if (r.getGerente() != null) {
            dto.setNombreGerente(r.getGerente().getNombre());
        }
        return dto;
    }
}
