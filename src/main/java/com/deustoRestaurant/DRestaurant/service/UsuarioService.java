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

@Service
public class UsuarioService {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private RestauranteDAO restauranteDAO;

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

    public List<UsuarioResponseDTO> obtenerPorRol(Rol rol) {
        return usuarioDAO.findByRol(rol)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<UsuarioResponseDTO> obtenerCamarerosPorRestaurante(Long restauranteId) {
        return usuarioDAO.findByRestauranteIdAndRol(restauranteId, Rol.CAMARERO)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UsuarioResponseDTO asignarRestaurante(Long usuarioId, Long restauranteId) {
        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Restaurante restaurante = restauranteDAO.findById(restauranteId)
                .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
        usuario.setRestaurante(restaurante);
        return toDTO(usuarioDAO.save(usuario));
    }

    public UsuarioResponseDTO desactivar(Long id) {
        Usuario usuario = usuarioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        return toDTO(usuarioDAO.save(usuario));
    }

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
