package com.plzgraduate.myongjigraduatebe.auth.api.token;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.request.TokenRequest;
import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.response.AccessTokenResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Token", description = "토큰 발급 API")
public interface TokenApiPresentation {

	AccessTokenResponse newToken(@Valid @RequestBody TokenRequest tokenRequest);
}
