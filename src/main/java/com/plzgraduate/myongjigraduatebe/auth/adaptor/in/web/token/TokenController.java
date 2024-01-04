package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.token;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.AccessTokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class TokenController implements TokenApiPresentation {

	private final TokenUseCase tokenUseCase;

	@PostMapping("/token")
	public AccessTokenResponse newToken(@Valid @RequestBody TokenRequest tokenRequest) {
		return tokenUseCase.createNewToken(TokenRequest.toCommand(tokenRequest.getRefreshToken()));
	}
}
