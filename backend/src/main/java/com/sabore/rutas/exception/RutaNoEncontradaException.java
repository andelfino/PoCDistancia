package com.sabore.rutas.exception;

public class RutaNoEncontradaException extends RuntimeException {

    public RutaNoEncontradaException() {
        super("TomTom no pudo calcular una ruta para las coordenadas indicadas");
    }
}
