package com.deustoRestaurant.DRestaurant.dto;

/**
 * DTO de solicitud para el inicio de sesión de un usuario.
 */
public class LoginRequestDTO {
    private String email;
    private String password;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}