package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestauranteDAO extends JpaRepository<Restaurante, Long> {
    List<Restaurante> findByActivo(boolean activo);
    List<Restaurante> findByGerenteId(Long gerenteId);
}
