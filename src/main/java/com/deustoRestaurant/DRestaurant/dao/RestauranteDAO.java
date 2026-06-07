package com.deustoRestaurant.DRestaurant.dao;

import com.deustoRestaurant.DRestaurant.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteDAO extends JpaRepository<Restaurante, Long> {
}