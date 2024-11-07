package com.plzgraduate.myongjigraduatebe.core;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvironmentCheck {

	private final Environment env;

	public EnvironmentCheck(Environment env) {
		this.env = env;
	}

	@PostConstruct
	public void init() {
		String url = env.getProperty("spring.datasource.url");
		String username = env.getProperty("spring.datasource.username");
		String password = env.getProperty("spring.datasource.password");
		String jwtKey = env.getProperty("jwt.secret-key");
		log.info("env url={}", url);
		log.info("env username={}", username);
		log.info("env password={}", password);
		log.info("env jwtKey={}", jwtKey);
	}
}
