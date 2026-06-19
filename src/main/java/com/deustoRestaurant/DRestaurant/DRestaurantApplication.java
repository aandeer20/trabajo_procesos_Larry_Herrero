package com.deustoRestaurant.DRestaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación DeustoRestaurant.
 * Arranca el contexto de Spring Boot.
 */
@SpringBootApplication
public class DRestaurantApplication {

	/**
	 * Método principal que inicia la aplicación.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(DRestaurantApplication.class, args);
	}

}
