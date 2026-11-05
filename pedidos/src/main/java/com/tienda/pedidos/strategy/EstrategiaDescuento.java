package com.tienda.pedidos.strategy;

/**
 * Interfaz Strategy para el cálculo de descuentos comerciales en pedidos.
 * Permite cumplir con el Principio Abierto/Cerrado (OCP): nuevas políticas
 * de descuento se incorporan creando nuevas implementaciones sin modificar
 * la clase de servicio ni la lógica de orquestación existente.
 */
public interface EstrategiaDescuento {

    /**
     * Retorna el porcentaje nominal de descuento aplicado por la estrategia.
     *
     * @return porcentaje decimal (ej: 0.15 para 15%)
     */
    double getPorcentaje();

    /**
     * Calcula el monto monetario del descuento sobre el subtotal proporcionado.
     *
     * @param subtotal valor base antes de impuestos y descuentos
     * @return monto monetario a descontar
     */
    double calcularDescuento(double subtotal);
}
