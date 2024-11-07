package com.plzgraduate.myongjigraduatebe.core;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/api/v1")
@Hidden
public class HealthCheckController {

	@GetMapping("/health")
	public String healthCheck() {
		return "healthy";
	}
}
