package com.sabore.rutas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de TomTom leídas desde application.yml.
 * La API Key se inyecta desde la variable de entorno TOMTOM_API_KEY
 * y nunca se hardcodea en el código fuente.
 */
@ConfigurationProperties(prefix = "tomtom")
public class TomTomProperties {

    private String apiKey;
    private String baseUrl;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
