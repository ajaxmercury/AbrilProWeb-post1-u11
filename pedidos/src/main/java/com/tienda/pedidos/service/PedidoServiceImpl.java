package com.tienda.pedidos.service;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.entity.Pedido;
import com.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.tienda.pedidos.repository.PedidoRepository;
import com.tienda.pedidos.strategy.EstrategiaDescuento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación de PedidoService aplicando Inversión de Dependencias (DIP)
 * y el Patrón Strategy para la resolución dinámica de políticas de descuento comercial.
 * Cumple con el Principio Abierto/Cerrado (OCP): nuevas estrategias se inyectan automáticamente
 * a través del mapa de beans de Spring sin modificar el código de este servicio.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final NotificacionService notificacionService;
    private final Map<String, EstrategiaDescuento> estrategias;

    // Inyección por constructor de dependencias y mapa dinámico de estrategias
    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             NotificacionService notificacionService,
                             Map<String, EstrategiaDescuento> estrategias) {
        this.pedidoRepository = pedidoRepository;
        this.notificacionService = notificacionService;
        this.estrategias = estrategias;
    }

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO) {
        double subtotal = requestDTO.getSubtotal();
        String tipoCliente = requestDTO.getTipoCliente() != null 
                ? requestDTO.getTipoCliente().trim().toUpperCase() 
                : "REGULAR";

        // Resolución dinámica de la estrategia de descuento (getOrDefault aplica fallback a REGULAR)
        EstrategiaDescuento estrategia = estrategias.getOrDefault(tipoCliente, estrategias.get("REGULAR"));

        double descuento = Math.round(estrategia.calcularDescuento(subtotal) * 100.0) / 100.0;
        double porcentajeDescuento = estrategia.getPorcentaje();
        double baseConDescuento = subtotal - descuento;
        double iva = Math.round((baseConDescuento * 0.19) * 100.0) / 100.0;
        double total = Math.round((baseConDescuento + iva) * 100.0) / 100.0;

        Pedido pedido = new Pedido();
        pedido.setClienteEmail(requestDTO.getClienteEmail().trim());
        pedido.setTipoCliente(tipoCliente);
        pedido.setSubtotal(subtotal);
        pedido.setPorcentajeDescuento(porcentajeDescuento);
        pedido.setDescuento(descuento);
        pedido.setIva(iva);
        pedido.setTotal(total);
        pedido.setNotasInternas(requestDTO.getNotasInternas());
        pedido.setFechaCreacion(LocalDateTime.now());

        Pedido guardado = pedidoRepository.save(pedido);

        // Notificación al cliente
        notificacionService.enviarNotificacion(guardado.getClienteEmail(),
                "Su pedido #" + guardado.getId() + " ha sido procesado exitosamente por un total de $" + total);

        return toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el pedido con identificador: " + id));
        return toResponseDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapeo privado y seguro de Entidad JPA a DTO de Respuesta.
     * GARANTÍA DE SEGURIDAD: El campo 'notasInternas' jamás se mapea al DTO.
     */
    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getClienteEmail(),
                pedido.getTipoCliente(),
                pedido.getSubtotal(),
                pedido.getPorcentajeDescuento(),
                pedido.getDescuento(),
                pedido.getIva(),
                pedido.getTotal(),
                pedido.getFechaCreacion()
        );
    }
}
