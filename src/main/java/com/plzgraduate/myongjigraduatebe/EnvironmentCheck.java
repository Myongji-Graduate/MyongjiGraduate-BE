package com.plzgraduate.myongjigraduatebe;

import javax.annotation.PostConstruct;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

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
		String a= env.getProperty("logging.level.p6spy");
		log.info("env url={}", url);
		log.info("env username={}", username);
		log.info("env password={}", password);
		log.info("env jwtKey={}", jwtKey);
		log.info("env level={}", a);
	}
}
