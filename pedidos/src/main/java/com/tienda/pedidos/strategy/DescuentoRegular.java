package com.tienda.pedidos.strategy;

import org.springframework.stereotype.Component;

/**
 * Estrategia de descuento para clientes regulares.
 * Aplica una tasa de descuento del 0% sobre el subtotal.
 */
@Component("REGULAR")
public class DescuentoRegular implements EstrategiaDescuento {

    @Override
    public double getPorcentaje() {
        return 0.0;
    }

    @Override
    public double calcularDescuento(double subtotal) {
        return 0.0;
    }
}
