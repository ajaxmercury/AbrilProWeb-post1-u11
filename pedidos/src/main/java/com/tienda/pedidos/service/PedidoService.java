package com.tienda.pedidos.service;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;

import java.util.List;

/**
 * Contrato de servicio para la gestión del ciclo de vida de los Pedidos.
 * Aplica el principio de Inversión de Dependencias (DIP) y Responsabilidad Única (SRP).
 */
public interface PedidoService {

    /**
     * Crea y procesa un nuevo pedido aplicando reglas de descuento, cálculo de impuestos
     * y persistencia transaccional.
     *
     * @param requestDTO datos validados de la solicitud de pedido
     * @return DTO de respuesta con los cálculos consolidados y sin notas internas
     */
    PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO);

    /**
     * Consulta un pedido por su identificador único.
     *
     * @param id identificador del pedido
     * @return DTO de respuesta del pedido
     * @throws com.tienda.pedidos.exception.RecursoNoEncontradoException si el pedido no existe
     */
    PedidoResponseDTO buscarPorId(Long id);

    /**
     * Lista la totalidad de los pedidos registrados.
     *
     * @return lista de pedidos en formato DTO de respuesta
     */
    List<PedidoResponseDTO> listarTodos();
}
