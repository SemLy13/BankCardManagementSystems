package com.example.bankcards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Конфигурация OpenAPI (Swagger) для банковской системы
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .servers(Arrays.asList(getLocalServer()))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT токен авторизации. Пример: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info getApiInfo() {
        return new Info()
                .title("Bank Cards API")
                .description("REST API для управления банковскими картами, пользователями и транзакциями")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Bank Cards Team")
                        .email("support@bankcards.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private Server getLocalServer() {
        return new Server()
                .url("http://localhost:8080" + contextPath)
                .description("Локальный сервер разработки");
    }
}
