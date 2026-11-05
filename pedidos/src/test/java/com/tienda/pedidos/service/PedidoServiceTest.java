package com.tienda.pedidos.service;

import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.entity.Pedido;
import com.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.tienda.pedidos.repository.PedidoRepository;
import com.tienda.pedidos.strategy.DescuentoMayorista;
import com.tienda.pedidos.strategy.DescuentoRegular;
import com.tienda.pedidos.strategy.DescuentoVip;
import com.tienda.pedidos.strategy.EstrategiaDescuento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private NotificacionService notificacionService;

    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        Map<String, EstrategiaDescuento> estrategias = new HashMap<>();
        estrategias.put("REGULAR", new DescuentoRegular());
        estrategias.put("VIP", new DescuentoVip());
        estrategias.put("MAYORISTA", new DescuentoMayorista());

        pedidoService = new PedidoServiceImpl(pedidoRepository, notificacionService, estrategias);
    }

    @Test
    @DisplayName("crearPedido con cliente REGULAR aplica 0% de descuento y liquida IVA 19%")
    void crearPedido_clienteRegular_calculaSinDescuento() {
        PedidoRequestDTO request = new PedidoRequestDTO("ana.martinez@correo.com", "REGULAR", 1000.0, "Nota interna secreta");

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.crearPedido(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getClienteEmail()).isEqualTo("ana.martinez@correo.com");
        assertThat(response.getTipoCliente()).isEqualTo("REGULAR");
        assertThat(response.getSubtotal()).isEqualTo(1000.0);
        assertThat(response.getPorcentajeDescuento()).isEqualTo(0.0);
        assertThat(response.getDescuento()).isEqualTo(0.0);
        assertThat(response.getIva()).isEqualTo(190.0);
        assertThat(response.getTotal()).isEqualTo(1190.0);

        verify(notificacionService).enviarNotificacion(eq("ana.martinez@correo.com"), anyString());
    }

    @Test
    @DisplayName("crearPedido con cliente VIP aplica 15% de descuento e IVA sobre la base reducida")
    void crearPedido_clienteVip_aplica15Porciento() {
        PedidoRequestDTO request = new PedidoRequestDTO("carlos.vip@empresa.com", "VIP", 1000.0, "Cliente premium");

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(20L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.crearPedido(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getTipoCliente()).isEqualTo("VIP");
        assertThat(response.getSubtotal()).isEqualTo(1000.0);
        assertThat(response.getPorcentajeDescuento()).isEqualTo(0.15);
        assertThat(response.getDescuento()).isEqualTo(150.0);
        assertThat(response.getIva()).isEqualTo(161.50); // (1000 - 150) * 0.19 = 850 * 0.19 = 161.50
        assertThat(response.getTotal()).isEqualTo(1011.50); // 850 + 161.50 = 1011.50

        verify(notificacionService).enviarNotificacion(eq("carlos.vip@empresa.com"), anyString());
    }

    @Test
    @DisplayName("crearPedido con cliente MAYORISTA aplica 25% de descuento e IVA sobre la base reducida")
    void crearPedido_clienteMayorista_aplica25Porciento() {
        PedidoRequestDTO request = new PedidoRequestDTO("distribuidora@mayor.com", "MAYORISTA", 2000.0, "Pallet 4");

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(30L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.crearPedido(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(30L);
        assertThat(response.getTipoCliente()).isEqualTo("MAYORISTA");
        assertThat(response.getSubtotal()).isEqualTo(2000.0);
        assertThat(response.getPorcentajeDescuento()).isEqualTo(0.25);
        assertThat(response.getDescuento()).isEqualTo(500.0); // 2000 * 0.25 = 500
        assertThat(response.getIva()).isEqualTo(285.0); // (2000 - 500) * 0.19 = 1500 * 0.19 = 285.0
        assertThat(response.getTotal()).isEqualTo(1785.0); // 1500 + 285.0 = 1785.0
    }

    @Test
    @DisplayName("crearPedido con tipo de cliente desconocido aplica fallback seguro a estrategia REGULAR")
    void crearPedido_tipoDesconocido_aplicaFallbackRegular() {
        PedidoRequestDTO request = new PedidoRequestDTO("usuario@nuevo.com", "CORPORATIVO_ESPECIAL", 500.0, null);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(40L);
            return p;
        });

        PedidoResponseDTO response = pedidoService.crearPedido(request);

        assertThat(response.getTipoCliente()).isEqualTo("REGULAR");
        assertThat(response.getPorcentajeDescuento()).isEqualTo(0.0);
        assertThat(response.getDescuento()).isEqualTo(0.0);
        assertThat(response.getTotal()).isEqualTo(595.0); // 500 + 19% IVA (95) = 595.0
    }

    @Test
    @DisplayName("Garantía de seguridad: notasInternas se guarda en entidad pero nunca se expone en DTO")
    void crearPedido_notasInternas_noExpuestasEnResponseDTO() {
        PedidoRequestDTO request = new PedidoRequestDTO("seguridad@tienda.com", "VIP", 800.0, "CLAVE_PRIVADA_BODEGA_123");

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> {
            Pedido p = inv.getArgument(0);
            p.setId(50L);
            return p;
        });

        pedidoService.crearPedido(request);

        // Verificamos que la entidad sí recibió la nota interna
        Pedido entidadGuardada = captor.getValue();
        assertThat(entidadGuardada.getNotasInternas()).isEqualTo("CLAVE_PRIVADA_BODEGA_123");

        // El DTO de respuesta no tiene getter para notasInternas ni lo contiene
        assertThat(PedidoResponseDTO.class.getDeclaredFields())
                .noneMatch(field -> field.getName().equals("notasInternas"));
    }

    @Test
    @DisplayName("buscarPorId cuando el pedido existe retorna el DTO mapeado")
    void buscarPorId_existe_retornaDTO() {
        Pedido pedido = new Pedido(1L, "cliente@test.com", "REGULAR", 100.0, 0.0, 0.0, 19.0, 119.0, "Nota", LocalDateTime.now());
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponseDTO dto = pedidoService.buscarPorId(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getClienteEmail()).isEqualTo("cliente@test.com");
    }

    @Test
    @DisplayName("buscarPorId cuando el pedido no existe lanza RecursoNoEncontradoException")
    void buscarPorId_noExiste_lanzaRecursoNoEncontradoException() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pedidoService.buscarPorId(999L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("listarTodos retorna colección de DTOs mapeados")
    void listarTodos_retornaColeccion() {
        Pedido p1 = new Pedido(1L, "c1@test.com", "REGULAR", 100.0, 0.0, 0.0, 19.0, 119.0, "N1", LocalDateTime.now());
        Pedido p2 = new Pedido(2L, "c2@test.com", "VIP", 200.0, 0.15, 30.0, 32.3, 202.3, "N2", LocalDateTime.now());
        when(pedidoRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PedidoResponseDTO> lista = pedidoService.listarTodos();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).getId()).isEqualTo(1L);
        assertThat(lista.get(1).getId()).isEqualTo(2L);
    }
}
