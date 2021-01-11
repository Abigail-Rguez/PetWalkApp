package com.example.petwalk.models;

public class WalksModel {

    private String idWalk, idPaseador, idPet, fecha, distancia, total, idDuenio;
    private int state;

    public WalksModel() {
    }

    public String getIdDuenio() {
        return idDuenio;
    }

    public void setIdDuenio(String idDuenio) {
        this.idDuenio = idDuenio;
    }

    public int getState() {
        return state;
    }

    public void setState(int estado) {
        this.state = estado;
    }

    public String getIdWalk() {
        return idWalk;
    }

    public void setIdWalk(String idWalk) {
        this.idWalk = idWalk;
    }

    public String getIdPaseador() {
        return idPaseador;
    }

    public void setIdPaseador(String idPaseador) {
        this.idPaseador = idPaseador;
    }

    public String getIdPet() {
        return idPet;
    }

    public void setIdPet(String idPet) {
        this.idPet = idPet;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
