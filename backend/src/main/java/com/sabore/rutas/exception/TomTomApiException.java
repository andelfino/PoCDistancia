package com.sabore.rutas.exception;

public class TomTomApiException extends RuntimeException {

    public TomTomApiException(String mensaje) {
        super(mensaje);
    }

    public TomTomApiException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
