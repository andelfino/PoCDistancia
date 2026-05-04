package com.sabore.rutas;

import com.sabore.rutas.exception.DireccionNoEncontradaException;
import com.sabore.rutas.exception.RutaNoEncontradaException;
import com.sabore.rutas.exception.TomTomApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.sabore.rutas")
public class RutasExceptionHandler {

    /** Dirección vacía o nula (@NotBlank falló) → 400 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .findFirst()
                .orElse("Solicitud inválida");
        return ResponseEntity.badRequest().body(Map.of("error", mensaje));
    }

    /** TomTom Geocoding no encontró resultados → 404 */
    @ExceptionHandler(DireccionNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleDireccionNoEncontrada(DireccionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    /** TomTom Routing no devolvió rutas → 422 */
    @ExceptionHandler(RutaNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleRutaNoEncontrada(RutaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", ex.getMessage()));
    }

    /** Error de red o HTTP al llamar a TomTom → 502 */
    @ExceptionHandler(TomTomApiException.class)
    public ResponseEntity<Map<String, String>> handleTomTomApi(TomTomApiException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
    }
}
