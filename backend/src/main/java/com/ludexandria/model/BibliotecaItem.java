package com.ludexandria.model;

import java.time.Instant;

public class BibliotecaItem {
    private Long id;
    private Long idUsuario;
    private Long idJuego;
    private String estado;
    private Double notaPersonal;
    private Double horasJugadas;
    private Instant fechaAnadido;
    private Juego juego;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public Long getIdJuego() { return idJuego; }
    public void setIdJuego(Long idJuego) { this.idJuego = idJuego; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getNotaPersonal() { return notaPersonal; }
    public void setNotaPersonal(Double notaPersonal) { this.notaPersonal = notaPersonal; }
    public Double getHorasJugadas() { return horasJugadas; }
    public void setHorasJugadas(Double horasJugadas) { this.horasJugadas = horasJugadas; }
    public Instant getFechaAnadido() { return fechaAnadido; }
    public void setFechaAnadido(Instant fechaAnadido) { this.fechaAnadido = fechaAnadido; }
    public Juego getJuego() { return juego; }
    public void setJuego(Juego juego) { this.juego = juego; }
}
