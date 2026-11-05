package com.tienda.pedidos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Modelo uniforme de respuesta para errores de la API REST (alineado con RFC 7807).
 * Centraliza la estructura de error retornada a los clientes HTTP en escenarios
 * de fallos de validación, recursos inexistentes o excepciones no controladas.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private int status;
    private String error;
    private String mensaje;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> validaciones;

    public ApiError() {
    }

    public ApiError(int status, String error, String mensaje, LocalDateTime timestamp, String path) {
        this(status, error, mensaje, timestamp, path, null);
    }

    public ApiError(int status, String error, String mensaje, LocalDateTime timestamp, String path,
                    Map<String, String> validaciones) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.path = path;
        this.validaciones = validaciones;
    }

    // Getters y Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getValidaciones() {
        return validaciones;
    }

    public void setValidaciones(Map<String, String> validaciones) {
        this.validaciones = validaciones;
    }
}
