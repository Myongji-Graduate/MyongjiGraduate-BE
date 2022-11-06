package com.plzgraduate.myongjigraduatebe.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableConfigurationProperties(value = {JwtProperties.class})
@RequiredArgsConstructor
public class JwtConfig {

  private final JwtProperties properties;

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
}
