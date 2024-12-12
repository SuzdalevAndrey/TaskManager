package ru.andreyszdlv.taskmanager.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Task Manager API",
                version = "1.0",
                description = "API для управления задачами, пользователями и комментариями.",
                contact = @Contact(
                        name = "Suzdalev Andrey",
                        email = "timurovichas@mail.ru"
                )
        )
)
@Configuration
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        name = "BearerAuth",
        description = "Используйте токен для авторизации. Пример: 'Authorization: Bearer <token>'"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Используйте JWT токен для доступа к API. Пример: 'Authorization: Bearer <token>'")
                        ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}