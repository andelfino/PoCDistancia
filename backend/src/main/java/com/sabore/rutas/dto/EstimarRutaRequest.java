package com.sabore.rutas.dto;

import jakarta.validation.constraints.NotBlank;

public class EstimarRutaRequest {

    @NotBlank(message = "La dirección del cliente no puede estar vacía")
    private String direccionCliente;

    public EstimarRutaRequest() {}

    public EstimarRutaRequest(String direccionCliente) {
        this.direccionCliente = direccionCliente;
    }

    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String direccionCliente) {
        this.direccionCliente = direccionCliente;
    }
}
