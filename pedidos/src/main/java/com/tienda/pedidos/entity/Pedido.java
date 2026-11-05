package com.tienda.pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa la persistencia de un Pedido en base de datos.
 * Encapsula el estado interno del pedido, incluyendo campos sensibles como 'notasInternas'
 * que no deben ser expuestos hacia las capas externas de la aplicación.
 */
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String clienteEmail;

    @Column(nullable = false, length = 30)
    private String tipoCliente;

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double porcentajeDescuento;

    @Column(nullable = false)
    private double descuento;

    @Column(nullable = false)
    private double iva;

    @Column(nullable = false)
    private double total;

    /**
     * Campo sensible: notas para uso interno de logística/ventas.
     * REGLA DE ORO: Jamás debe exponerse en PedidoResponseDTO ni registrarse en logs.
     */
    @Column(length = 500)
    private String notasInternas;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public Pedido() {
    }

    public Pedido(Long id, String clienteEmail, String tipoCliente, double subtotal,
                  double porcentajeDescuento, double descuento, double iva, double total,
                  String notasInternas, LocalDateTime fechaCreacion) {
        this.id = id;
        this.clienteEmail = clienteEmail;
        this.tipoCliente = tipoCliente;
        this.subtotal = subtotal;
        this.porcentajeDescuento = porcentajeDescuento;
        this.descuento = descuento;
        this.iva = iva;
        this.total = total;
        this.notasInternas = notasInternas;
        this.fechaCreacion = fechaCreacion;
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
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

    public String getNotasInternas() {
        return notasInternas;
    }

    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
