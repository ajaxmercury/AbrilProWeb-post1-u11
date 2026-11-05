package com.tienda.pedidos.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstrategiaDescuentoTest {

    private final EstrategiaDescuento regular = new DescuentoRegular();
    private final EstrategiaDescuento vip = new DescuentoVip();
    private final EstrategiaDescuento mayorista = new DescuentoMayorista();

    @Test
    @DisplayName("DescuentoRegular debe aplicar 0% de descuento y retornar 0.0")
    void descuentoRegular_calculaCero() {
        assertThat(regular.getPorcentaje()).isEqualTo(0.0);
        assertThat(regular.calcularDescuento(1000.0)).isEqualTo(0.0);
        assertThat(regular.calcularDescuento(0.0)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("DescuentoVip debe aplicar 15% de descuento sobre subtotal")
    void descuentoVip_calcula15Porciento() {
        assertThat(vip.getPorcentaje()).isEqualTo(0.15);
        assertThat(vip.calcularDescuento(1000.0)).isEqualTo(150.0);
        assertThat(vip.calcularDescuento(200.0)).isEqualTo(30.0);
        assertThat(vip.calcularDescuento(0.0)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("DescuentoMayorista debe aplicar 25% de descuento sobre subtotal")
    void descuentoMayorista_calcula25Porciento() {
        assertThat(mayorista.getPorcentaje()).isEqualTo(0.25);
        assertThat(mayorista.calcularDescuento(1000.0)).isEqualTo(250.0);
        assertThat(mayorista.calcularDescuento(400.0)).isEqualTo(100.0);
        assertThat(mayorista.calcularDescuento(0.0)).isEqualTo(0.0);
    }
}
