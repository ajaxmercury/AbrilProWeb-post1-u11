package com.tienda.pedidos.strategy;

import org.springframework.stereotype.Component;

/**
 * Estrategia de descuento para clientes mayoristas.
 * Aplica una tasa de descuento por volumen del 25% (0.25) sobre el subtotal.
 */
@Component("MAYORISTA")
public class DescuentoMayorista implements EstrategiaDescuento {

    private static final double PORCENTAJE = 0.25;

    @Override
    public double getPorcentaje() {
        return PORCENTAJE;
    }

    @Override
    public double calcularDescuento(double subtotal) {
        if (subtotal <= 0) {
            return 0.0;
        }
        return subtotal * PORCENTAJE;
    }
}
