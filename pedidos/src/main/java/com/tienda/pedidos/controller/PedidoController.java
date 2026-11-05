package com.tienda.pedidos.controller;

import com.tienda.pedidos.dto.ApiError;
import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la gestión comercial de Pedidos.
 * Cumple con el Principio de Responsabilidad Única (SRP) y delega la lógica de negocio
 * íntegramente a PedidoService mediante inyección por constructor.
 * Totalmente documentado con anotaciones OpenAPI 3 / Swagger.
 */
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Operaciones comerciales para el ciclo de vida de los pedidos: registro con cálculo de descuentos dinámicos (Strategy), consulta individual y listado general.")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(
            summary = "Crear y procesar un nuevo pedido",
            description = "Valida el payload de entrada, aplica dinámicamente la estrategia de descuento (REGULAR: 0%, VIP: 15%, MAYORISTA: 25%), " +
                    "liquida el IVA (19%), persiste el pedido y envía la notificación asíncrona al cliente. " +
                    "Garantiza el aislamiento seguro excluyendo 'notasInternas' de la respuesta."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pedido creado, liquidado y persistido exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación en los campos del cuerpo de solicitud (ej. subtotal <= 0, email inválido o tipo vacío).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error inesperado no controlado en el servidor.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<PedidoResponseDTO> crearPedido(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos requeridos para procesar el pedido.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PedidoRequestDTO.class))
            )
            @Valid @RequestBody PedidoRequestDTO requestDTO) {
        PedidoResponseDTO nuevoPedido = pedidoService.crearPedido(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar un pedido por su identificador único (ID)",
            description = "Recupera la información consolidada de un pedido registrado a partir de su ID autonumérico. Si no existe, retorna 404 Not Found."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido encontrado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un pedido registrado con el identificador proporcionado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @Parameter(description = "Identificador único (ID) del pedido a consultar", example = "1", required = true)
            @PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping
    @Operation(
            summary = "Listar la totalidad de pedidos registrados",
            description = "Retorna una colección con todos los pedidos procesados en el sistema en formato DTO sin exponer campos confidenciales."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de pedidos obtenido correctamente.",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PedidoResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }
}
