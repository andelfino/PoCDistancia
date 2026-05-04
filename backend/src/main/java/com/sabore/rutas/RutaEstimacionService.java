package com.sabore.rutas;

import com.fasterxml.jackson.databind.JsonNode;
import com.sabore.rutas.config.TomTomProperties;
import com.sabore.rutas.dto.EstimarRutaResponse;
import com.sabore.rutas.exception.DireccionNoEncontradaException;
import com.sabore.rutas.exception.RutaNoEncontradaException;
import com.sabore.rutas.exception.TomTomApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class RutaEstimacionService {

    // Coordenadas fijas del origen: Shopping Tres Cruces, Montevideo, Uruguay
    private static final String ORIGEN_NOMBRE = "Shopping Tres Cruces, Montevideo, Uruguay";
    private static final double ORIGEN_LAT = -34.893611;
    private static final double ORIGEN_LON = -56.166389;

    private final RestTemplate restTemplate;
    private final TomTomProperties tomTomProps;

    public RutaEstimacionService(RestTemplate restTemplate, TomTomProperties tomTomProps) {
        this.restTemplate = restTemplate;
        this.tomTomProps = tomTomProps;
    }

    /**
     * Orquesta el flujo completo:
     * 1. Geocodifica la dirección del cliente con TomTom Search API.
     * 2. Calcula la ruta desde Tres Cruces hasta ese punto con TomTom Routing API.
     * 3. Devuelve distancia y duración estimada.
     */
    public EstimarRutaResponse estimar(String direccionCliente) {
        double[] destino = geocodificarDireccion(direccionCliente);
        long[] rutaInfo = calcularRuta(destino[0], destino[1]);

        long distanciaMetros = rutaInfo[0];
        long duracionSegundos = rutaInfo[1];
        double distanciaKm = Math.round(distanciaMetros / 10.0) / 100.0;
        int duracionMinutos = (int) Math.ceil(duracionSegundos / 60.0);

        return new EstimarRutaResponse(
                ORIGEN_NOMBRE,
                direccionCliente,
                destino[0],
                destino[1],
                distanciaMetros,
                distanciaKm,
                duracionSegundos,
                duracionMinutos
        );
    }

    // ─── TomTom Search / Geocoding ───────────────────────────────────────────

    /**
     * Convierte una dirección en texto a coordenadas geográficas.
     * Endpoint: GET /search/2/geocode/{query}.json?key=...&limit=1
     *
     * La dirección se pasa como segmento de path con encode() explícito para
     * que caracteres especiales (espacios, tildes, etc.) queden correctamente
     * percent-encoded. Las barras (/) se reemplazan antes para evitar que
     * UriComponentsBuilder las interprete como separadores de path.
     *
     * @return arreglo [lat, lon] del primer resultado
     */
    private double[] geocodificarDireccion(String direccion) {
        // Reemplazar "/" por espacio para evitar que parta el path en segmentos
        String direccionSegura = direccion.replace("/", " ");

        URI uri = UriComponentsBuilder
                .fromHttpUrl(tomTomProps.getBaseUrl())
                .path("/search/2/geocode/{query}.json")
                .queryParam("key", tomTomProps.getApiKey())
                .queryParam("limit", 1)
                .buildAndExpand(direccionSegura)
                .encode()
                .toUri();

        JsonNode respuesta = llamarTomTom(uri);

        JsonNode results = respuesta.path("results");
        if (results.isMissingNode() || results.isEmpty()) {
            throw new DireccionNoEncontradaException(direccion);
        }

        JsonNode posicion = results.get(0).path("position");
        double lat = posicion.path("lat").asDouble();
        double lon = posicion.path("lon").asDouble();

        return new double[]{lat, lon};
    }

    // ─── TomTom Routing ──────────────────────────────────────────────────────

    /**
     * Calcula la ruta desde el origen fijo (Tres Cruces) hasta el destino.
     * Endpoint: GET /routing/1/calculateRoute/{orig}:{dest}/json?key=...&travelMode=car
     *
     * @return arreglo [distanciaMetros, duracionSegundos]
     */
    private long[] calcularRuta(double destLat, double destLon) {
        String ubicaciones = ORIGEN_LAT + "," + ORIGEN_LON + ":" + destLat + "," + destLon;

        URI uri = UriComponentsBuilder
                .fromHttpUrl(tomTomProps.getBaseUrl())
                .path("/routing/1/calculateRoute/{ubicaciones}/json")
                .queryParam("key", tomTomProps.getApiKey())
                .queryParam("travelMode", "car")
                .buildAndExpand(ubicaciones)
                .encode()
                .toUri();

        JsonNode respuesta = llamarTomTom(uri);

        JsonNode routes = respuesta.path("routes");
        if (routes.isMissingNode() || routes.isEmpty()) {
            throw new RutaNoEncontradaException();
        }

        JsonNode summary = routes.get(0).path("summary");
        long distanciaMetros = summary.path("lengthInMeters").asLong();
        long duracionSegundos = summary.path("travelTimeInSeconds").asLong();

        return new long[]{distanciaMetros, duracionSegundos};
    }

    // ─── Llamada HTTP genérica ────────────────────────────────────────────────

    private JsonNode llamarTomTom(URI uri) {
        try {
            JsonNode respuesta = restTemplate.getForObject(uri, JsonNode.class);
            if (respuesta == null) {
                throw new TomTomApiException("TomTom devolvió una respuesta vacía");
            }
            return respuesta;
        } catch (RestClientException e) {
            throw new TomTomApiException("Error al consultar TomTom: " + e.getMessage(), e);
        }
    }
}
