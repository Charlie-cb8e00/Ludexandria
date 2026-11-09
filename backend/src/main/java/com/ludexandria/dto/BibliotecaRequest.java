package com.ludexandria.dto;

import jakarta.validation.constraints.NotNull;

public class BibliotecaRequest {
    @NotNull
    private Long idJuego;
    private String estado = "pendiente";
    private Double notaPersonal;
    private Double horasJugadas;

    public Long getIdJuego() { return idJuego; }
    public void setIdJuego(Long idJuego) { this.idJuego = idJuego; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getNotaPersonal() { return notaPersonal; }
    public void setNotaPersonal(Double notaPersonal) { this.notaPersonal = notaPersonal; }
    public Double getHorasJugadas() { return horasJugadas; }
    public void setHorasJugadas(Double horasJugadas) { this.horasJugadas = horasJugadas; }
}
