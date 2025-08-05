package com.plzgraduate.myongjigraduatebe.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Myongji-Graduate API Documentation")
                        .description("졸업을 부탁해 서비스의 API 명세서입니다.")
                        .version("v2.0.0"))
                .components(securityComponents())
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("AccessToken")); // 🔒 added
    }

    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes(
                        "AccessToken",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );
    }
}
