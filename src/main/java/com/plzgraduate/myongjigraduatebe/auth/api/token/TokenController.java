package com.plzgraduate.myongjigraduatebe.auth.api.token;

import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.request.TokenRequest;
import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.response.AccessTokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.usecase.token.TokenUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class TokenController implements TokenApiPresentation {

	private final TokenUseCase tokenUseCase;

	@PostMapping("/token")
	public AccessTokenResponse createNewToken(@Valid @RequestBody TokenRequest tokenRequest) {
		return tokenUseCase.generateNewToken(tokenRequest.getRefreshToken());
	}
}
