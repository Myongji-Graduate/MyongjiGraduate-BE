package com.plzgraduate.myongjigraduatebe.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")
class JwtProperties {
	private String issuer;
	private String secretKey;
	private int expirySeconds;
}
