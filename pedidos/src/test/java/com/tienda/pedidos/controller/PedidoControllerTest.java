package com.tienda.pedidos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.pedidos.dto.PedidoRequestDTO;
import com.tienda.pedidos.dto.PedidoResponseDTO;
import com.tienda.pedidos.exception.GlobalExceptionHandler;
import com.tienda.pedidos.exception.RecursoNoEncontradoException;
import com.tienda.pedidos.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PedidoController.class)
@Import(GlobalExceptionHandler.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService pedidoService;

    @Test
    @DisplayName("POST /api/pedidos con datos válidos retorna 201 Created y PedidoResponseDTO")
    void post_pedidoValido_retorna201() throws Exception {
        PedidoRequestDTO request = new PedidoRequestDTO("carlos.vip@empresa.com", "VIP", 1000.0, "Urgente");
        PedidoResponseDTO response = new PedidoResponseDTO(1L, "carlos.vip@empresa.com", "VIP",
                1000.0, 0.15, 150.0, 161.50, 1011.50, LocalDateTime.now());

        when(pedidoService.crearPedido(any(PedidoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clienteEmail").value("carlos.vip@empresa.com"))
                .andExpect(jsonPath("$.tipoCliente").value("VIP"))
                .andExpect(jsonPath("$.subtotal").value(1000.0))
                .andExpect(jsonPath("$.descuento").value(150.0))
                .andExpect(jsonPath("$.iva").value(161.50))
                .andExpect(jsonPath("$.total").value(1011.50))
                .andExpect(jsonPath("$.notasInternas").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/pedidos con subtotal negativo retorna 400 Bad Request y ApiError estructurado")
    void post_subtotalNegativo_retorna400() throws Exception {
        PedidoRequestDTO request = new PedidoRequestDTO("carlos@empresa.com", "VIP", -50.0, "Nota");

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.validaciones.subtotal").exists());
    }

    @Test
    @DisplayName("POST /api/pedidos con email con formato inválido retorna 400 Bad Request")
    void post_emailInvalido_retorna400() throws Exception {
        PedidoRequestDTO request = new PedidoRequestDTO("email-sin-arroba", "REGULAR", 200.0, null);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validaciones.clienteEmail").exists());
    }

    @Test
    @DisplayName("POST /api/pedidos con tipo de cliente en blanco retorna 400 Bad Request")
    void post_tipoClienteBlanco_retorna400() throws Exception {
        PedidoRequestDTO request = new PedidoRequestDTO("valido@correo.com", "   ", 200.0, null);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validaciones.tipoCliente").exists());
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} cuando existe retorna 200 OK y DTO")
    void get_pedidoExiste_retorna200() throws Exception {
        PedidoResponseDTO response = new PedidoResponseDTO(1L, "cliente@test.com", "REGULAR",
                500.0, 0.0, 0.0, 95.0, 595.0, LocalDateTime.now());

        when(pedidoService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(595.0));
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} cuando no existe retorna 404 Not Found y ApiError")
    void get_pedidoNoExiste_retorna404() throws Exception {
        when(pedidoService.buscarPorId(999L))
                .thenThrow(new RecursoNoEncontradoException("No se encontró el pedido con identificador: 999"));

        mockMvc.perform(get("/api/pedidos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value("No se encontró el pedido con identificador: 999"))
                .andExpect(jsonPath("$.path").value("/api/pedidos/999"));
    }

    @Test
    @DisplayName("GET /api/pedidos retorna 200 OK y arreglo de pedidos")
    void get_listarTodos_retorna200() throws Exception {
        PedidoResponseDTO p1 = new PedidoResponseDTO(1L, "c1@test.com", "REGULAR", 100.0, 0.0, 0.0, 19.0, 119.0, LocalDateTime.now());
        PedidoResponseDTO p2 = new PedidoResponseDTO(2L, "c2@test.com", "VIP", 200.0, 0.15, 30.0, 32.3, 202.3, LocalDateTime.now());

        when(pedidoService.listarTodos()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}
