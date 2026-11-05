package com.tienda.pedidos.exception;

/**
 * Excepción de dominio que señala la ausencia de un recurso solicitado en la base de datos.
 * Es capturada y transformada en un código de estado HTTP 404 Not Found por el manejador global.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
