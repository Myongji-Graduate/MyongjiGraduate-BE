package com.plzgraduate.myongjigraduatebe.auth.api.signin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.request.SignInRequest;
import com.plzgraduate.myongjigraduatebe.core.exception.UnAuthorizedException;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class SignInControllerTest extends WebAdaptorTestSupport {

	@DisplayName("로그인을 진행한다.")
	@Test
	void signIn() throws Exception {
		//given
		SignInRequest request = SignInRequest.builder()
			.authId("mju-graduate")
			.password("1q2w3e4r!")
			.build();

		//when //then
		mockMvc.perform(
				post("/api/v1/auth/sign-in")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("아이디가 빈 문자열일 경우 에러를 반환한다.")
	@Test
	void blankAuthId() throws Exception {
		//given
		SignInRequest request = SignInRequest.builder()
			.authId("  ")
			.password("1q2w3e4r!")
			.build();
		//when //then
		mockMvc.perform(
				post("/api/v1/auth/sign-in")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("아이디를 입력해주세요."));
	}

	@DisplayName("비밀번호가 빈 문자열일 경우 에러를 반환한다.")
	@Test
	void blankPassword() throws Exception {
		//given
		SignInRequest request = SignInRequest.builder()
			.authId("mju-graduate")
			.password("")
			.build();
		//when //then
		mockMvc.perform(
				post("/api/v1/auth/sign-in")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("비밀번호를 입력해주세요."));
	}

	@DisplayName("아이디 및 비밀번호에 해당하는 사용자가 없을 경우 에러를 반환한다.")
	@Test
	void invalidUser() throws Exception {
		//given
		SignInRequest request = SignInRequest.builder()
			.authId("mju-graduate")
			.password("1q2w3e4r")
			.build();
		given(signInUseCase.signIn(any(), any())).willThrow(
			new UnAuthorizedException("아이디 혹은 비밀번호가 일치하지 않습니다."));
		//when //then
		mockMvc.perform(
				post("/api/v1/auth/sign-in")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.errorCode").value("아이디 혹은 비밀번호가 일치하지 않습니다."));
	}
}
