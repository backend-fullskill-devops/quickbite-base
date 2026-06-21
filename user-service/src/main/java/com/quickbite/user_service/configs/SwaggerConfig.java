package com.quickbite.user_service.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "User Service API", version = "v1", description = "API documentation for User Service"))
public class SwaggerConfig {
    // No conditional beans – Swagger UI is always enabled.
}
