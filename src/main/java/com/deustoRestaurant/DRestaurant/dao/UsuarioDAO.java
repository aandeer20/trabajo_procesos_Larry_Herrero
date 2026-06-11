package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 * Proporciona consultas por email, rol, estado activo y restaurante asignado.
 */

public interface UsuarioDAO extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByRolAndActivo(Rol rol, boolean activo);
    List<Usuario> findByRestauranteIdAndRol(Long restauranteId, Rol rol);
}
