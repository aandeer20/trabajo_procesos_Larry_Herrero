package com.deustoRestaurant.DRestaurant;

import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.entity.Rol;
import com.deustoRestaurant.DRestaurant.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Override
    public void run(String... args) throws Exception {

        // CLIENTES
        crearUsuario("Carlos García", "carlos@email.com", "1234", Rol.CLIENTE);
        crearUsuario("Ana Martínez", "ana@email.com", "1234", Rol.CLIENTE);
        crearUsuario("Luis Fernández", "luis@email.com", "1234", Rol.CLIENTE);

        // CAMAREROS
        crearUsuario("Pedro Ruiz", "pedro@email.com", "1234", Rol.CAMARERO);
        crearUsuario("María López", "maria@email.com", "1234", Rol.CAMARERO);

        // GERENTES
        crearUsuario("Elena Torres", "elena@email.com", "1234", Rol.GERENTE);
    }

    private void crearUsuario(String nombre, String email, String password, Rol rol) {
        if (usuarioDAO.findByEmail(email).isEmpty()) {
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setEmail(email);
            u.setPassword(password);
            u.setRol(rol);
            usuarioDAO.save(u);
        }
    }
}