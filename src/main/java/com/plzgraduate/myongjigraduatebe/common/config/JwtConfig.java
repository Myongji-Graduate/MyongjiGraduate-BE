package com.plzgraduate.myongjigraduatebe.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.algorithms.Algorithm;

@Configuration
@EnableConfigurationProperties(value = {JwtProperties.class})
public class JwtConfig {

  private final JwtProperties properties;
  private final Algorithm algorithm;

  public JwtConfig(JwtProperties properties) {
    this.properties = properties;
    this.algorithm = Algorithm.HMAC512(properties.getClientSecret());
  }

  public String getHeader() {
    return properties.getHeader();
  }

  public String getPrefix() {
    return properties.getPrefix();
  }

  public String getIssuer() {
    return properties.getIssuer();
  }

  public String getClientSecret() {
    return properties.getClientSecret();
  }

  public int getExpirySeconds() {
    return properties.getExpirySeconds();
  }

  public Algorithm getAlgorithm() {
    return algorithm;
  }
}
