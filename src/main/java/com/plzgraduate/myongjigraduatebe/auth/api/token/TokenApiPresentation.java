package com.plzgraduate.myongjigraduatebe.auth.api.token;

import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.request.TokenRequest;
import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.response.AccessTokenResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Token", description = "토큰 발급 API")
public interface TokenApiPresentation {

	AccessTokenResponse createNewToken(@Valid @RequestBody TokenRequest tokenRequest);
}
