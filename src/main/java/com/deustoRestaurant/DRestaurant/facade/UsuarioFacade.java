package com.deustoRestaurant.DRestaurant.facade;

import com.deustoRestaurant.DRestaurant.dto.UsuarioRequestDTO;
import com.deustoRestaurant.DRestaurant.dto.UsuarioResponseDTO;
import com.deustoRestaurant.DRestaurant.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioFacade {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public UsuarioResponseDTO registrar(@RequestBody UsuarioRequestDTO dto) {
        return usuarioService.registrar(dto);
    }

    @GetMapping("/camareros")
    public List<UsuarioResponseDTO> obtenerCamareros() {
        return usuarioService.obtenerCamareros();
    }

    @DeleteMapping("/{id}")
    public UsuarioResponseDTO desactivar(@PathVariable Long id) {
        return usuarioService.desactivar(id);
    }
}