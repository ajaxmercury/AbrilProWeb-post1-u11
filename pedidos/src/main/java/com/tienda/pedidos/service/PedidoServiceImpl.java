package com.tienda.pedidos.service;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.entity.Pedido;
import com.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.tienda.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de PedidoService aplicando Inversión de Dependencias (DIP).
 * Todos los colaboradores (PedidoRepository, NotificacionService) son finales e inyectados
 * a través del constructor, eliminando el acoplamiento rígido del operador 'new'.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final NotificacionService notificacionService;

    // Inyección explícita por constructor (DIP)
    public PedidoServiceImpl(PedidoRepository pedidoRepository, NotificacionService notificacionService) {
        this.pedidoRepository = pedidoRepository;
        this.notificacionService = notificacionService;
    }

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO requestDTO) {
        double subtotal = requestDTO.getSubtotal();
        String tipoCliente = requestDTO.getTipoCliente().trim().toUpperCase();

        // Cálculo temporal base antes de extraer a patrón Strategy (Commit 4)
        double porcentajeDescuento = 0.0;
        if ("VIP".equalsIgnoreCase(tipoCliente)) {
            porcentajeDescuento = 0.15;
        } else if ("MAYORISTA".equalsIgnoreCase(tipoCliente)) {
            porcentajeDescuento = 0.25;
        }

        double descuento = subtotal * porcentajeDescuento;
        double baseConDescuento = subtotal - descuento;
        double iva = baseConDescuento * 0.19;
        double total = baseConDescuento + iva;

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

        // Envío desacoplado de notificación
        notificacionService.enviarNotificacion(guardado.getClienteEmail(),
                "Su pedido #" + guardado.getId() + " ha sido creado exitosamente.");

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
     * Mapeo privado y seguro de Entidad a DTO de Respuesta.
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
