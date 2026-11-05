package com.tienda.pedidos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para la solicitud de creación de un nuevo Pedido.
 * Aplica validaciones de Bean Validation para garantizar la integridad
 * de los datos antes de que alcancen la lógica de negocio.
 */
public class PedidoRequestDTO {

    @NotBlank(message = "El email del cliente es obligatorio")
    @Email(message = "El formato del email de cliente no es válido")
    private String clienteEmail;

    @NotBlank(message = "El tipo de cliente es obligatorio (ej: REGULAR, VIP, MAYORISTA)")
    private String tipoCliente;

    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser un valor positivo mayor que cero")
    private Double subtotal;

    /**
     * Notas internas opcionales proporcionadas en la creación.
     * Esta información es sensible y jamás debe retornarse en la respuesta.
     */
    private String notasInternas;

    public PedidoRequestDTO() {
    }

    public PedidoRequestDTO(String clienteEmail, String tipoCliente, Double subtotal, String notasInternas) {
        this.clienteEmail = clienteEmail;
        this.tipoCliente = tipoCliente;
        this.subtotal = subtotal;
        this.notasInternas = notasInternas;
    }

    // Getters y Setters
    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public String getNotasInternas() {
        return notasInternas;
    }

    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
    }
}
