package com.plzgraduate.myongjigraduatebe.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ConfigurationProperties("jwt")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class JwtProperties {

  private final String header;

  private final String prefix;

  private final String issuer;

  private final String clientSecret;

  private final int expirySeconds;
}
