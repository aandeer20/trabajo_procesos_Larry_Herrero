package com.deustoRestaurant.DRestaurant;

import com.deustoRestaurant.DRestaurant.dao.ReservaDAO;
import com.deustoRestaurant.DRestaurant.dao.RestauranteDAO;
import com.deustoRestaurant.DRestaurant.dao.UsuarioDAO;
import com.deustoRestaurant.DRestaurant.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private RestauranteDAO restauranteDAO;
    @Autowired private ReservaDAO reservaDAO;

    @Override
    public void run(String... args) throws Exception {

        // GERENTES
        Usuario elena = crearUsuario("Elena Torres", "elena@email.com", "1234", Rol.GERENTE);
        Usuario marcos = crearUsuario("Marcos Vidal", "marcos@email.com", "1234", Rol.GERENTE);

        // RESTAURANTES
        Restaurante r1 = crearRestaurante("DeustoRestaurant", "Calle Lehendakari Aguirre 83, Bilbao",
                "944 000 001", "13:00-16:00", "20:00-23:00", 30, 20, elena);
        Restaurante r2 = crearRestaurante("La Terraza Vasca", "Calle Gran Vía 45, Bilbao",
                "944 000 002", "13:00-16:00", "20:00-23:00", 25, 15, marcos);

        // CLIENTES
        Usuario carlos = crearUsuario("Carlos García", "carlos@email.com", "1234", Rol.CLIENTE);
        Usuario ana    = crearUsuario("Ana Martínez",  "ana@email.com",    "1234", Rol.CLIENTE);
        Usuario luis   = crearUsuario("Luis Fernández","luis@email.com",   "1234", Rol.CLIENTE);

        // CAMAREROS
        Usuario pedro = crearUsuarioConRestaurante("Pedro Ruiz",   "pedro@email.com", "1234", Rol.CAMARERO, r1);
        Usuario maria = crearUsuarioConRestaurante("María López",  "maria@email.com", "1234", Rol.CAMARERO, r1);
        Usuario javi  = crearUsuarioConRestaurante("Javier Ortiz", "javi@email.com",  "1234", Rol.CAMARERO, r2);

        // RESERVAS DE PRUEBA
        crearReserva(carlos, r1, LocalDate.now(),           Turno.COMIDA, 2, pedro);
        crearReserva(ana,    r1, LocalDate.now(),           Turno.CENA,   4, maria);
        crearReserva(luis,   r2, LocalDate.now(),           Turno.COMIDA, 3, javi);
        crearReserva(carlos, r1, LocalDate.now().plusDays(1), Turno.CENA, 2, null);
        crearReserva(ana,    r2, LocalDate.now().plusDays(2), Turno.COMIDA, 5, null);
    }

    private Usuario crearUsuario(String nombre, String email, String password, Rol rol) {
        return usuarioDAO.findByEmail(email).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setEmail(email);
            u.setPassword(password);
            u.setRol(rol);
            u.setActivo(true);
            return usuarioDAO.save(u);
        });
    }

    private Usuario crearUsuarioConRestaurante(String nombre, String email, String password, Rol rol, Restaurante restaurante) {
        return usuarioDAO.findByEmail(email).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setEmail(email);
            u.setPassword(password);
            u.setRol(rol);
            u.setActivo(true);
            u.setRestaurante(restaurante);
            return usuarioDAO.save(u);
        });
    }

    private Restaurante crearRestaurante(String nombre, String direccion, String telefono,
                                          String horarioComida, String horarioCena,
                                          int aforoComida, int aforoCena, Usuario gerente) {
        return restauranteDAO.findAll().stream()
                .filter(r -> r.getNombre().equals(nombre))
                .findFirst()
                .orElseGet(() -> {
                    Restaurante r = new Restaurante();
                    r.setNombre(nombre);
                    r.setDireccion(direccion);
                    r.setTelefono(telefono);
                    r.setHorarioComida(horarioComida);
                    r.setHorarioCena(horarioCena);
                    r.setAforoMaximoComida(aforoComida);
                    r.setAforoMaximoCena(aforoCena);
                    r.setActivo(true);
                    r.setGerente(gerente);
                    return restauranteDAO.save(r);
                });
    }

    private void crearReserva(Usuario cliente, Restaurante restaurante,
                               LocalDate fecha, Turno turno, int comensales, Usuario camarero) {
        Reserva r = new Reserva();
        r.setCliente(cliente);
        r.setRestaurante(restaurante);
        r.setFecha(fecha);
        r.setTurno(turno);
        r.setNumComensales(comensales);
        r.setEstado(EstadoReserva.CONFIRMADA);
        r.setCamarero(camarero);
        reservaDAO.save(r);
    }
}