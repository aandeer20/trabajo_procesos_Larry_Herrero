package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de usuarios.
 * Cubre registro, autenticación, consultas por rol y operaciones CRUD sobre {@link Usuario}.
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RestauranteDAO restauranteDAO;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param dto datos del usuario a crear
     * @return el usuario creado como {@link UsuarioResponseDTO}
     * @throws RuntimeException si el email ya está en uso
     */
    public UsuarioResponseDTO registrar(UsuarioRequestDTO dto) {
        if (usuarioDAO.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(dto.getRol());
        if (dto.getRestauranteId() != null) {
            Restaurante restaurante = restauranteDAO.findById(dto.getRestauranteId())
                    .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
            usuario.setRestaurante(restaurante);
        }
        return toDTO(usuarioDAO.save(usuario));
    }

    /**
     * Autentica a un usuario con email y contraseña.
     *
     * @param email    dirección de correo del usuario
     * @param password contraseña en texto plano
     * @return el usuario autenticado como {@link UsuarioResponseDTO}
     * @throws RuntimeException si las credenciales son incorrectas o el usuario está desactivado
     */
    public UsuarioResponseDTO login(String email, String password) {
        Usuario usuario = usuarioDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));
        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Credenciales incorrectas");
        }
        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario desactivado");
        }
        return toDTO(usuario);
    }

    /**
     * Devuelve todos los usuarios con el rol indicado.
     *
     * @param rol rol por el que filtrar
     * @return lista de usuarios con ese rol
     */
    public List<UsuarioResponseDTO> obtenerPorRol(Rol rol) {
        return usuarioDAO.findByRol(rol)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve los camareros asignados a un restaurante concreto.
     *
     * @param restauranteId identificador del restaurante
     * @return lista de camareros del restaurante
     */
    public List<UsuarioResponseDTO> obtenerCamarerosPorRestaurante(Long restauranteId) {
        return usuarioDAO.findByRestauranteIdAndRol(restauranteId, Rol.CAMARERO)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Asigna un restaurante a un usuario existente.
     *
     * @param usuarioId     identificador del usuario
     * @param restauranteId identificador del restaurante
     * @return el usuario actualizado
     * @throws RuntimeException si el usuario o el restaurante no existen
     */
    public UsuarioResponseDTO asignarRestaurante(Long usuarioId, Long restauranteId) {
        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Restaurante restaurante = restauranteDAO.findById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        usuario.setRestaurante(restaurante);
        return toDTO(usuarioDAO.save(usuario));
    }

    /**
     * Actualiza parcialmente los datos de un usuario.
     * Solo se modifican los campos no nulos del DTO.
     * La contraseña solo se actualiza si no está en blanco.
     *
     * @param id  identificador del usuario a actualizar
     * @param dto campos que se desean modificar
     * @return el usuario actualizado
     * @throws RuntimeException si el usuario o el restaurante indicado no existen
     */
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (dto.getNombre()    != null) usuario.setNombre(dto.getNombre());
        if (dto.getApellidos() != null) usuario.setApellidos(dto.getApellidos());
        if (dto.getTelefono()  != null) usuario.setTelefono(dto.getTelefono());
        if (dto.getRol()       != null) usuario.setRol(dto.getRol());
        if (dto.getPassword()  != null && !dto.getPassword().isBlank())
            usuario.setPassword(dto.getPassword());
        if (dto.getRestauranteId() != null) {
            Restaurante restaurante = restauranteDAO.findById(dto.getRestauranteId())
                    .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
            usuario.setRestaurante(restaurante);
        }
        return toDTO(usuarioDAO.save(usuario));
    }

    /**
     * Reactiva un usuario previamente desactivado.
     *
     * @param id identificador del usuario
     * @return el usuario con {@code activo = true}
     * @throws RuntimeException si el usuario no existe
     */
    public UsuarioResponseDTO activar(Long id) {
        Usuario usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(true);
        return toDTO(usuarioDAO.save(usuario));
    }

    /**
     * Desactiva lógicamente un usuario (baja suave).
     *
     * @param id identificador del usuario
     * @return el usuario con {@code activo = false}
     * @throws RuntimeException si el usuario no existe
     */
    public UsuarioResponseDTO desactivar(Long id) {
        Usuario usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        return toDTO(usuarioDAO.save(usuario));
    }

    /**
     * Convierte una entidad {@link Usuario} en su DTO de respuesta.
     *
     * @param u entidad a convertir
     * @return DTO con los datos públicos del usuario
     */
    private UsuarioResponseDTO toDTO(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellidos(u.getApellidos());
        dto.setEmail(u.getEmail());
        dto.setTelefono(u.getTelefono());
        dto.setRol(u.getRol());
        dto.setActivo(u.isActivo());
        if (u.getRestaurante() != null) {
            dto.setNombreRestaurante(u.getRestaurante().getNombre());
        }
        return dto;
    }
}
