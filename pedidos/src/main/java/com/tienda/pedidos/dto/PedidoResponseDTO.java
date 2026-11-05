package com.tienda.pedidos.dto;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta pública de un Pedido.
 * Aisla la entidad JPA de la capa externa de la API REST y garantiza
 * que bajo ninguna circunstancia se expongan campos sensibles como 'notasInternas'.
 */
public class PedidoResponseDTO {

    private Long id;
    private String clienteEmail;
    private String tipoCliente;
    private double subtotal;
    private double porcentajeDescuento;
    private double descuento;
    private double iva;
    private double total;
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
