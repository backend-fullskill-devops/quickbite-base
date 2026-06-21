package com.quickbite.restaurant_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Restaurant Service API", version = "v1", description = "API documentation for Restaurant Service"))
public class SwaggerConfig {
    // Swagger UI is always enabled.
}
