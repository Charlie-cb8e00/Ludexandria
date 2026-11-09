package com.ludexandria.model;

import java.time.Instant;

public class Usuario {
    private Long id;
    private String email;
    private String passwordHash;
    private String nombre;
    private String rol; // USER | ADMIN
    private Instant fechaAlta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public Instant getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(Instant fechaAlta) { this.fechaAlta = fechaAlta; }
}
