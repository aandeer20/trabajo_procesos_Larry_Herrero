package com.deustoRestaurant.DRestaurant.service;

import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioResponseDTO;
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

    public UsuarioResponseDTO registrar(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setRol(dto.getRol());
        Usuario guardado = usuarioDAO.save(usuario);
        return toDTO(guardado);
    }

    public List<UsuarioResponseDTO> obtenerCamareros() {
        return usuarioDAO.findByRol(Rol.CAMARERO)
                .stream().map(this::toDTO).collect(Collectors.toList());
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
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        dto.setActivo(u.isActivo());
        return dto;
    }
}