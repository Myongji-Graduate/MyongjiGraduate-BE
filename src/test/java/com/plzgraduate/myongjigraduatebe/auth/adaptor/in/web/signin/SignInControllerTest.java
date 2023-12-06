package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.signin;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;

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

	@DisplayName("아이디가 6자리 이상이어야한다.")
	@Test
	void wrongSizeAuthId() throws Exception {
		//given
		SignInRequest request = SignInRequest.builder()
			.authId("messi")
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
			.andExpect(jsonPath("$.message").value("아이디는 6자에서 20자 사이여야합니다."));
	}

	@DisplayName("비밀번호는 8자리 이상이어야 한다.")
	@Test
	void wrongSizePassword() throws Exception {
		//given
		SignInRequest request = SignInRequest.builder()
			.authId("mju-graduate")
			.password("1q2w3e")
			.build();
		//when //then
		mockMvc.perform(
				post("/api/v1/auth/sign-in")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("비밀번호는 특수문자를 포함한 8자에서 20자 사이여야합니다."));
	}
}
