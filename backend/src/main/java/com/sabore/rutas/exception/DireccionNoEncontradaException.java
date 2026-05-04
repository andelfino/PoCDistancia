package com.sabore.rutas.exception;

public class DireccionNoEncontradaException extends RuntimeException {

    public DireccionNoEncontradaException(String direccion) {
        super("No se encontró la dirección: \"" + direccion + "\"");
    }
}
