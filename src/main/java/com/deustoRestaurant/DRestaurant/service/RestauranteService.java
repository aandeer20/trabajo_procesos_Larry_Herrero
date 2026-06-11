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

/**
 * Servicio de gestión de restaurantes.
 * Permite crear, consultar, actualizar aforos y desactivar {@link Restaurante}.
 */
@Service
public class RestauranteService {

    @Autowired
    private RestauranteDAO restauranteDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    /**
     * Crea un nuevo restaurante y, si se indica un gerente, lo asocia al restaurante.
     *
     * @param dto datos del restaurante a crear
     * @return el restaurante creado como {@link RestauranteResponseDTO}
     * @throws RuntimeException si el gerente indicado no existe
     */
    public RestauranteResponseDTO crear(RestauranteRequestDTO dto) {
        Usuario gerente = null;
        if (dto.getGerenteId() != null) {
            gerente = usuarioDAO.findById(dto.getGerenteId())
                    .orElseThrow(() -> new RuntimeException("Gerente no encontrado"));
        }

        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(dto.getNombre());
        restaurante.setDireccion(dto.getDireccion());
        restaurante.setTelefono(dto.getTelefono());
        restaurante.setHorarioComida(dto.getHorarioComida());
        restaurante.setHorarioCena(dto.getHorarioCena());
        restaurante.setAforoMaximoComida(dto.getAforoMaximoComida());
        restaurante.setAforoMaximoCena(dto.getAforoMaximoCena());
        restaurante.setActivo(true);
        if (gerente != null) {
            restaurante.setGerente(gerente);
        }
        Restaurante saved = restauranteDAO.save(restaurante);

        if (gerente != null) {
            gerente.setRestaurante(saved);
            usuarioDAO.save(gerente);
        }
        return toDTO(saved);
    }

    /**
     * Devuelve todos los restaurantes registrados en el sistema.
     *
     * @return lista completa de restaurantes
     */
    public List<RestauranteResponseDTO> obtenerTodos() {
        return restauranteDAO.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve únicamente los restaurantes activos.
     *
     * @return lista de restaurantes con {@code activo = true}
     */
    public List<RestauranteResponseDTO> obtenerActivos() {
        return restauranteDAO.findByActivo(true)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Actualiza los datos generales de un restaurante.
     *
     * @param id  identificador del restaurante a actualizar
     * @param dto nuevos datos del restaurante
     * @return el restaurante actualizado
     * @throws RuntimeException si el restaurante no existe
     */
    public RestauranteResponseDTO actualizar(Long id, RestauranteRequestDTO dto) {
        Restaurante restaurante = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        restaurante.setNombre(dto.getNombre());
        restaurante.setDireccion(dto.getDireccion());
        restaurante.setTelefono(dto.getTelefono());
        restaurante.setHorarioComida(dto.getHorarioComida());
        restaurante.setHorarioCena(dto.getHorarioCena());
        restaurante.setAforoMaximoComida(dto.getAforoMaximoComida());
        restaurante.setAforoMaximoCena(dto.getAforoMaximoCena());
        return toDTO(restauranteDAO.save(restaurante));
    }

    /**
     * Actualiza el aforo máximo del turno de comida de un restaurante.
     *
     * @param id    identificador del restaurante
     * @param aforo nuevo aforo máximo para comida
     * @return el restaurante con el aforo actualizado
     * @throws RuntimeException si el restaurante no existe
     */
    public RestauranteResponseDTO actualizarAforoComida(Long id, int aforo) {
        Restaurante restaurante = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        restaurante.setAforoMaximoComida(aforo);
        return toDTO(restauranteDAO.save(restaurante));
    }

    /**
     * Actualiza el aforo máximo del turno de cena de un restaurante.
     *
     * @param id    identificador del restaurante
     * @param aforo nuevo aforo máximo para cena
     * @return el restaurante con el aforo actualizado
     * @throws RuntimeException si el restaurante no existe
     */
    public RestauranteResponseDTO actualizarAforoCena(Long id, int aforo) {
        Restaurante restaurante = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        restaurante.setAforoMaximoCena(aforo);
        return toDTO(restauranteDAO.save(restaurante));
    }

    /**
     * Desactiva lógicamente un restaurante (baja suave).
     *
     * @param id identificador del restaurante
     * @return el restaurante con {@code activo = false}
     * @throws RuntimeException si el restaurante no existe
     */
    public RestauranteResponseDTO desactivar(Long id) {
        Restaurante restaurante = restauranteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        restaurante.setActivo(false);
        return toDTO(restauranteDAO.save(restaurante));
    }

    /**
     * Convierte una entidad {@link Restaurante} en su DTO de respuesta.
     *
     * @param r entidad a convertir
     * @return DTO con los datos públicos del restaurante
     */
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
