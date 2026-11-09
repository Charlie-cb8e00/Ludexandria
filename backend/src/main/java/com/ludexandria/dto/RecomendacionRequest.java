package com.ludexandria.dto;

public class RecomendacionRequest {
    private int tiempo = 60;
    private String plataforma = "";
    private String animo = "";

    public int getTiempo() { return tiempo; }
    public void setTiempo(int tiempo) { this.tiempo = tiempo; }
    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
    public String getAnimo() { return animo; }
    public void setAnimo(String animo) { this.animo = animo; }
}
