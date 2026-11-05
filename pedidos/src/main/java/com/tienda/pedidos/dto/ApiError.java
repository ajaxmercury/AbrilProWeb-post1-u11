package com.tienda.pedidos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Modelo uniforme de respuesta para errores de la API REST (alineado con RFC 7807).
 * Centraliza la estructura de error retornada a los clientes HTTP en escenarios
 * de fallos de validación, recursos inexistentes o excepciones no controladas.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estructura estándar de respuesta para errores de la API REST (RFC 7807)")
public class ApiError {

    @Schema(description = "Código de estado HTTP", example = "400")
    private int status;

    @Schema(description = "Nombre o razón estándar del estado HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mensaje comprensible y detallado del error producido",
            example = "La solicitud contiene errores de validación en uno o más campos.")
    private String mensaje;

    @Schema(description = "Marca de tiempo exacta en que se generó el fallo", example = "2026-11-05T14:20:00")
    private LocalDateTime timestamp;

    @Schema(description = "Ruta URI del endpoint invocado en el que se originó el error", example = "/api/pedidos")
    private String path;

    @Schema(description = "Mapa asociativo campo-mensaje con los detalles de violaciones de validación (solo presente en 400)",
            example = "{\"subtotal\":\"El subtotal debe ser un valor positivo mayor que cero\"}")
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
