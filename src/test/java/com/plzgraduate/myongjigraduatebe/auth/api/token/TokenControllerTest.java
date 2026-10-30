package com.plzgraduate.myongjigraduatebe.auth.api.token;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.request.TokenRequest;
import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.response.AccessTokenResponse;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class TokenControllerTest extends WebAdaptorTestSupport {

	@DisplayName("AccessToken 재발급한다.")
	@Test
	void newToken() throws Exception {
		//given
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";
		TokenRequest request = TokenRequest.builder()
			.refreshToken(refreshToken)
			.build();

		AccessTokenResponse response = AccessTokenResponse.builder()
			.accessToken(accessToken)
			.build();
		given(tokenUseCase.generateNewToken(any())).willReturn(response);
		//when //then
		mockMvc.perform(
				post("/api/v1/auth/token")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken", is(accessToken)));
	}

	@DisplayName("만료된 refreshToken")
	@Test
	void invalidRefreshToken() throws Exception {
		//given
		String refreshToken = "refreshToken";
		TokenRequest request = TokenRequest.builder()
			.refreshToken(refreshToken)
			.build();
		given(tokenUseCase.generateNewToken(any())).willThrow(
			new IllegalArgumentException("유효하지 않은 토큰입니다."));

		//when //then
		mockMvc.perform(
				post("/api/v1/auth/token")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode", is("유효하지 않은 토큰입니다.")));
	}
}
