package com.tienda.pedidos.strategy;

import org.springframework.stereotype.Component;

/**
 * Estrategia de descuento para clientes VIP.
 * Aplica una tasa de descuento preferencial del 15% (0.15) sobre el subtotal.
 */
@Component("VIP")
public class DescuentoVip implements EstrategiaDescuento {

    private static final double PORCENTAJE = 0.15;

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
