package com.ludexandria.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class BibliotecaRequest {
    @NotNull
    private Long idJuego;
    @Pattern(regexp = "pendiente|jugando|completado|abandonado")
    private String estado = "pendiente";
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double notaPersonal;
    @DecimalMin("0.0")
    private Double horasJugadas;
    // Distingue "no cambiar la nota" de enviar null para borrarla.
    private boolean notaPersonalPresente;

    public Long getIdJuego() { return idJuego; }
    public void setIdJuego(Long idJuego) { this.idJuego = idJuego; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getNotaPersonal() { return notaPersonal; }
    public void setNotaPersonal(Double notaPersonal) {
        this.notaPersonal = notaPersonal;
        this.notaPersonalPresente = true;
    }
    public boolean isNotaPersonalPresente() { return notaPersonalPresente; }
    public Double getHorasJugadas() { return horasJugadas; }
    public void setHorasJugadas(Double horasJugadas) { this.horasJugadas = horasJugadas; }
}
