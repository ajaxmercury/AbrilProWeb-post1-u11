package com.tienda.pedidos.service;

/**
 * Contrato de abstracción para el envío de notificaciones.
 * Cumple con el Principio de Inversión de Dependencias (DIP) al permitir
 * que los servicios de alto nivel dependan de esta interfaz y no de
 * implementaciones concretas como clientes SMTP o librerías externas.
 */
public interface NotificacionService {

    /**
     * Envía una notificación de confirmación de pedido al cliente.
     *
     * @param destinatario correo electrónico o identificador del destinatario
     * @param mensaje contenido del mensaje de notificación
     */
    void enviarNotificacion(String destinatario, String mensaje);
}
