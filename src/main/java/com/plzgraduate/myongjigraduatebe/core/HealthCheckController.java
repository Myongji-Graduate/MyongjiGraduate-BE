package com.plzgraduate.myongjigraduatebe.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;

@WebAdapter
@RequestMapping("/api/v1")
public class HealthCheckController {

	@GetMapping("/health")
	public String healthCheck() {
		return "healthy";
	}
}
