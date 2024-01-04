package com.plzgraduate.myongjigraduatebe.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI springOpenApi() {
		return new OpenAPI().info(new Info()
			.title("Myongji-Graduate API Documentation")
			.description("졸업을 부탁해 서비스의 API 명세서입니다.")
			.version("v2.0.0"));
	}
}
