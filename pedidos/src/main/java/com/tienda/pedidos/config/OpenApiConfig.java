package com.tienda.pedidos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada de OpenAPI 3 y Swagger UI para el servicio de Pedidos.
 * Expone la documentación interactiva y metadatos de la API en /swagger-ui.html.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API REST de Gestión de Pedidos",
                version = "1.0.0",
                description = "Servicio empresarial de procesamiento de pedidos refactorizado con principios SOLID, " +
                        "aislamiento mediante DTOs, patrón Strategy dinámico para descuentos y endurecimiento con " +
                        "manejo centralizado de excepciones y logging estructurado.",
                contact = @Contact(
                        name = "Equipo de Arquitectura Backend",
                        email = "arquitectura@tienda.com",
                        url = "https://tienda.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor Local de Desarrollo (H2)")
        }
)
public class OpenApiConfig {
}
