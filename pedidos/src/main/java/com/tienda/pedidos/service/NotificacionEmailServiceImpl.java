package com.tienda.pedidos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementación concreta del servicio de notificación simulando envío de correo electrónico.
 * Se gestiona como un Bean de Spring inyectable, eliminando por completo la instanciación
 * manual mediante el operador 'new'.
 */
@Service
public class NotificacionEmailServiceImpl implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionEmailServiceImpl.class);

    @Override
    public void enviarNotificacion(String destinatario, String mensaje) {
        log.info("Simulando envío de correo electrónico a: {} | Asunto: {}", destinatario, mensaje);
    }
}
