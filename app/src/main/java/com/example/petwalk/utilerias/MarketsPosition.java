package com.example.petwalk.utilerias;

public class MarketsPosition {

    private double longitud, latitude;
    private String nombre;

    public MarketsPosition(double latitude, double longitud, String nombre) {
        this.longitud = longitud;
        this.latitude = latitude;
        this.nombre = nombre;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
