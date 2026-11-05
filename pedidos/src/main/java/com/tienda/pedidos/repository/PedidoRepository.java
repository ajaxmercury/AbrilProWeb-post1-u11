package com.tienda.pedidos.repository;

import com.tienda.pedidos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad Pedido.
 * Reemplaza la implementación heredada con JdbcTemplate y SQL concatenado,
 * proporcionando operaciones CRUD seguras, paginación y consultas tipadas
 * libres de vulnerabilidades por Inyección SQL.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Búsqueda tipada y parametrizada por el correo electrónico del cliente.
     *
     * @param clienteEmail correo del cliente a consultar
     * @return lista de pedidos asociados al cliente
     */
    List<Pedido> findByClienteEmail(String clienteEmail);

    /**
     * Búsqueda tipada y parametrizada por el tipo de cliente.
     *
     * @param tipoCliente tipo o categoría del cliente (REGULAR, VIP, MAYORISTA)
     * @return lista de pedidos coincidentes
     */
    List<Pedido> findByTipoCliente(String tipoCliente);
}
