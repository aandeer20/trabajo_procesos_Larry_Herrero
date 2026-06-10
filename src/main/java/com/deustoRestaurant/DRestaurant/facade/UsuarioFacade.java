package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.LoginRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioResponseDTO;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioFacade {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrar(@RequestBody UsuarioRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        try {
            return ResponseEntity.ok(usuarioService.login(dto.getEmail(), dto.getPassword()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/rol/{rol}")
    public List<UsuarioResponseDTO> obtenerPorRol(@PathVariable Rol rol) {
        return usuarioService.obtenerPorRol(rol);
    }

    @GetMapping("/restaurante/{restauranteId}/camareros")
    public List<UsuarioResponseDTO> obtenerCamarerosPorRestaurante(@PathVariable Long restauranteId) {
        return usuarioService.obtenerCamarerosPorRestaurante(restauranteId);
    }

    @PutMapping("/{usuarioId}/restaurante/{restauranteId}")
    public UsuarioResponseDTO asignarRestaurante(@PathVariable Long usuarioId, @PathVariable Long restauranteId) {
        return usuarioService.asignarRestaurante(usuarioId, restauranteId);
    }

    @DeleteMapping("/{id}")
    public UsuarioResponseDTO desactivar(@PathVariable Long id) {
        return usuarioService.desactivar(id);
    }
}