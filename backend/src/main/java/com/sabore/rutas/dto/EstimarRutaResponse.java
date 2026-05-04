package com.sabore.rutas.dto;

public class EstimarRutaResponse {

    private String origen;
    private String destino;
    private double destinoLat;
    private double destinoLon;
    private long distanciaMetros;
    private double distanciaKm;
    private long duracionSegundos;
    private int duracionMinutos;

    public EstimarRutaResponse() {}

    public EstimarRutaResponse(
            String origen,
            String destino,
            double destinoLat,
            double destinoLon,
            long distanciaMetros,
            double distanciaKm,
            long duracionSegundos,
            int duracionMinutos) {
        this.origen = origen;
        this.destino = destino;
        this.destinoLat = destinoLat;
        this.destinoLon = destinoLon;
        this.distanciaMetros = distanciaMetros;
        this.distanciaKm = distanciaKm;
        this.duracionSegundos = duracionSegundos;
        this.duracionMinutos = duracionMinutos;
    }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public double getDestinoLat() { return destinoLat; }
    public void setDestinoLat(double destinoLat) { this.destinoLat = destinoLat; }

    public double getDestinoLon() { return destinoLon; }
    public void setDestinoLon(double destinoLon) { this.destinoLon = destinoLon; }

    public long getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(long distanciaMetros) { this.distanciaMetros = distanciaMetros; }

    public double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(double distanciaKm) { this.distanciaKm = distanciaKm; }

    public long getDuracionSegundos() { return duracionSegundos; }
    public void setDuracionSegundos(long duracionSegundos) { this.duracionSegundos = duracionSegundos; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
}
