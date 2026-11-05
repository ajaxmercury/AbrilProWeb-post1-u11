package com.tienda.pedidos.service;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.entity.Pedido;
import com.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.tienda.pedidos.repository.PedidoRepository;
import com.tienda.pedidos.strategy.EstrategiaDescuento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación de PedidoService aplicando Inversión de Dependencias (DIP),
 * Patrón Strategy para descuentos dinámicos y Logging estructurado mediante SLF4J.
 *
 * REGLA DE ORO DE SEGURIDAD: El campo 'notasInternas' jamás es registrado en los logs
 * ni expuesto en los DTOs de respuesta hacia los clientes de la API REST.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final NotificacionService notificacionService;
    private final Map<String, EstrategiaDescuento> estrategias;

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
        String tipoEntrada = requestDTO.getTipoCliente() != null ? requestDTO.getTipoCliente().trim().toUpperCase() : "REGULAR";

        log.info("Iniciando procesamiento de pedido para cliente email: {} | Tipo solicitado: {} | Subtotal base: ${}",
                requestDTO.getClienteEmail(), tipoEntrada, subtotal);

        // Resolución de la estrategia de descuento con fallback advertido
        EstrategiaDescuento estrategia = estrategias.get(tipoEntrada);
        String tipoClienteFinal = tipoEntrada;
        if (estrategia == null) {
            log.warn("Tipo de cliente '{}' no reconocido en el catálogo de estrategias. Aplicando fallback a estrategia REGULAR (0% desc).", tipoEntrada);
            estrategia = estrategias.get("REGULAR");
            tipoClienteFinal = "REGULAR";
        }

        double porcentajeDescuento = estrategia.getPorcentaje();
        double descuento = Math.round(estrategia.calcularDescuento(subtotal) * 100.0) / 100.0;
        double baseConDescuento = Math.round((subtotal - descuento) * 100.0) / 100.0;
        double iva = Math.round((baseConDescuento * 0.19) * 100.0) / 100.0;
        double total = Math.round((baseConDescuento + iva) * 100.0) / 100.0;

        log.debug("Cálculo financiero detallado - Subtotal: ${} | Descuento aplicado: ${} ({}%) | Base imponible: ${} | IVA (19%): ${} | Total: ${}",
                subtotal, descuento, porcentajeDescuento * 100, baseConDescuento, iva, total);

        Pedido pedido = new Pedido();
        pedido.setClienteEmail(requestDTO.getClienteEmail().trim());
        pedido.setTipoCliente(tipoClienteFinal);
        pedido.setSubtotal(subtotal);
        pedido.setPorcentajeDescuento(porcentajeDescuento);
        pedido.setDescuento(descuento);
        pedido.setIva(iva);
        pedido.setTotal(total);
        // Se preserva en base de datos para uso interno, pero NUNCA se registra en log
        pedido.setNotasInternas(requestDTO.getNotasInternas());
        pedido.setFechaCreacion(LocalDateTime.now());

        Pedido guardado = pedidoRepository.save(pedido);

        log.info("Pedido guardado exitosamente con ID: {} | Total liquidado: ${} | Estrategia: {}",
                guardado.getId(), guardado.getTotal(), tipoClienteFinal);

        // Envío desacoplado de la notificación
        notificacionService.enviarNotificacion(guardado.getClienteEmail(),
                "Su pedido #" + guardado.getId() + " ha sido confirmado por un valor total de $" + total);

        return toResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        log.debug("Consultando pedido con identificador ID: {}", id);

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> {
            log.warn("Pedido con ID {} no encontrado en base de datos", id);
            return new RecursoNoEncontradoException("No se encontró el pedido con identificador: " + id);
        });

        log.debug("Pedido con ID {} recuperado satisfactoriamente", id);
        return toResponseDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        log.debug("Iniciando consulta para listar la totalidad de pedidos");
        List<Pedido> pedidos = pedidoRepository.findAll();
        log.info("Listado de pedidos completado. Total de registros encontrados: {}", pedidos.size());

        return pedidos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapeo privado y seguro de Entidad JPA a DTO de Respuesta.
     * GARANTÍA DE SEGURIDAD: El campo 'notasInternas' jamás se incluye en el DTO expuesto.
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
