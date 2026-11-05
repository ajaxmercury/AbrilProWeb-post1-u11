package com.tienda.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta pública de un Pedido.
 * Aísla la entidad JPA de la capa externa de la API REST y garantiza
 * que bajo ninguna circunstancia se expongan campos sensibles como 'notasInternas'.
 */
@Schema(description = "Estructura de respuesta que consolida el pedido procesado, liquidado y seguro")
public class PedidoResponseDTO {

    @Schema(description = "Identificador único autonumérico del pedido", example = "1")
    private Long id;

    @Schema(description = "Correo electrónico del cliente", example = "carlos.mendoza@empresa.com")
    private String clienteEmail;

    @Schema(description = "Tipo o categoría comercial del cliente aplicado", example = "VIP")
    private String tipoCliente;

    @Schema(description = "Monto monetario base original (subtotal)", example = "1000.00")
    private double subtotal;

    @Schema(description = "Porcentaje de descuento aplicado según la estrategia ejecutada", example = "0.15")
    private double porcentajeDescuento;

    @Schema(description = "Monto monetario total liquidado como descuento comercial", example = "150.00")
    private double descuento;

    @Schema(description = "Impuesto sobre el Valor Añadido (IVA 19%) liquidado sobre la base con descuento", example = "161.50")
    private double iva;

    @Schema(description = "Valor total a pagar por el cliente (baseConDescuento + IVA)", example = "1011.50")
    private double total;

    @Schema(description = "Fecha y hora en que se creó y persistió el pedido", example = "2026-11-05T14:15:30")
    private LocalDateTime fechaCreacion;

    public PedidoResponseDTO() {
    }

    public PedidoResponseDTO(Long id, String clienteEmail, String tipoCliente,
                             double subtotal, double porcentajeDescuento,
                             double descuento, double iva, double total,
                             LocalDateTime fechaCreacion) {
        this.id = id;
        this.clienteEmail = clienteEmail;
        this.tipoCliente = tipoCliente;
        this.subtotal = subtotal;
        this.porcentajeDescuento = porcentajeDescuento;
        this.descuento = descuento;
        this.iva = iva;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
