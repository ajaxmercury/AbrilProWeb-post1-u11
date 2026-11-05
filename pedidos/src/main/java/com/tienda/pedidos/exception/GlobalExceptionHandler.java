package com.tienda.pedidos.exception;

import com.tienda.pedidos.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador centralizado de excepciones mediante @RestControllerAdvice.
 * Intercepta todas las excepciones lanzadas a lo largo de las capas de controlador,
 * servicio y persistencia, eliminando los bloques try/catch dispersos y garantizando
 * una respuesta estandarizada con código de estado HTTP adecuado y estructura ApiError.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Intercepta excepciones de recursos no encontrados (404 Not Found).
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex,
                                                              HttpServletRequest request) {
        log.warn("Recurso no encontrado en ruta {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Intercepta fallos de validación de Bean Validation en DTOs entrantes (400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(MethodArgumentNotValidException ex,
                                                      HttpServletRequest request) {
        Map<String, String> detalles = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            detalles.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Fallo de validación en {} para {} campos: {}",
                request.getRequestURI(), detalles.size(), detalles);

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "La solicitud contiene errores de validación en uno o más campos.",
                LocalDateTime.now(),
                request.getRequestURI(),
                detalles
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Intercepta argumentos ilegales o violaciones de reglas de negocio en la capa de servicio (400 Bad Request).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> manejarIllegalArgument(IllegalArgumentException ex,
                                                           HttpServletRequest request) {
        log.warn("Argumento inválido en {}: {}", request.getRequestURI(), ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Intercepta cualquier otra excepción imprevista no controlada (500 Internal Server Error).
     * Oculta detalles internos de infraestructura o stacktraces al cliente por motivos de seguridad.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manejarExcepcionGenerica(Exception ex,
                                                             HttpServletRequest request) {
        log.error("Error no controlado al procesar solicitud en {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error interno en el servidor. Por favor comuníquese con soporte técnico.",
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
