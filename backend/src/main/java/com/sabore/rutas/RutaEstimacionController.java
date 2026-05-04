package com.sabore.rutas;

import com.sabore.rutas.dto.EstimarRutaRequest;
import com.sabore.rutas.dto.EstimarRutaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rutas")
public class RutaEstimacionController {

    private final RutaEstimacionService rutaEstimacionService;

    public RutaEstimacionController(RutaEstimacionService rutaEstimacionService) {
        this.rutaEstimacionService = rutaEstimacionService;
    }

    /**
     * Estima la distancia y duración del viaje desde Shopping Tres Cruces
     * hasta la dirección ingresada por el cliente.
     *
     * POST /api/rutas/estimar
     * Body: { "direccionCliente": "Av. 18 de Julio 1234, Montevideo" }
     */
    @PostMapping("/estimar")
    public ResponseEntity<EstimarRutaResponse> estimar(@Valid @RequestBody EstimarRutaRequest request) {
        EstimarRutaResponse response = rutaEstimacionService.estimar(request.getDireccionCliente());
        return ResponseEntity.ok(response);
    }
}
