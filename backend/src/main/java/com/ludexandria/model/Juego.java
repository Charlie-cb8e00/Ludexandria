package com.ludexandria.model;

import java.time.LocalDate;
import java.time.Instant;

public class Juego {
    private Long id;
    private Long igdbId;
    private String nombre;
    private String resumen;
    private LocalDate fechaSalida;
    private Double notaIgdb;
    private Integer duracionMinutos;
    private String urlPortada;
    private Instant ultimaActualizacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIgdbId() { return igdbId; }
    public void setIgdbId(Long igdbId) { this.igdbId = igdbId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public Double getNotaIgdb() { return notaIgdb; }
    public void setNotaIgdb(Double notaIgdb) { this.notaIgdb = notaIgdb; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public String getUrlPortada() { return urlPortada; }
    public void setUrlPortada(String urlPortada) { this.urlPortada = urlPortada; }
    public Instant getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(Instant ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }
}
