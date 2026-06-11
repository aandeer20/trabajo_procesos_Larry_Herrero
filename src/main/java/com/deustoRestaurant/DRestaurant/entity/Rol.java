package com.deustoRestaurant.DRestaurant.entity;

/**
 * Roles disponibles para los usuarios del sistema.
 */
public enum Rol {
    /** Cliente que realiza reservas. */
    CLIENTE,
    /** Camarero asignado a reservas de un restaurante. */
    CAMARERO,
    /** Gerente responsable de un restaurante. */
    GERENTE
}