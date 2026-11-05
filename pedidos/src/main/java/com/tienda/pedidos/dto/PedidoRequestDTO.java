package com.tienda.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para la solicitud de creación de un nuevo Pedido.
 * Aplica validaciones de Bean Validation y documentación OpenAPI 3 / Swagger.
 */
@Schema(description = "Cuerpo de solicitud (payload) para la creación y procesamiento de un nuevo pedido")
public class PedidoRequestDTO {

    @Schema(description = "Correo electrónico corporativo o personal del cliente",
            example = "carlos.mendoza@empresa.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El email del cliente es obligatorio")
    @Email(message = "El formato del email de cliente no es válido")
    private String clienteEmail;

    @Schema(description = "Tipo o segmento comercial del cliente para el cálculo del descuento",
            example = "VIP",
            allowableValues = {"REGULAR", "VIP", "MAYORISTA"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El tipo de cliente es obligatorio (ej: REGULAR, VIP, MAYORISTA)")
    private String tipoCliente;

    @Schema(description = "Monto monetario base del pedido antes de aplicar descuentos e impuestos",
            example = "1000.00",
            minimum = "0.01",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser un valor positivo mayor que cero")
    private Double subtotal;

    @Schema(description = "Notas y observaciones internas para el equipo de logística y ventas. " +
            "DATO SENSIBLE: Esta información se persiste en base de datos pero JAMÁS se retorna en el DTO de respuesta ni se registra en logs.",
            example = "Prioridad de despacho 1; cliente preferencial regional norte.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
