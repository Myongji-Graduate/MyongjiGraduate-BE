package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.signup.SignUpController;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.signup.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.AuthIdDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.CheckAuthIdDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.signup.SignUpUseCase;

@WebMvcTest(controllers = SignUpController.class)
class SignUpControllerTest extends WebAdaptorTestSupport {

	@MockBean
	private SignUpUseCase signUpUseCase;
	@MockBean
	private CheckAuthIdDuplicationUseCase checkAuthIdDuplicationUseCase;

	@DisplayName("회원가입을 진행한다.")
	@Test
	void signUp() throws Exception {
		//given
		SignUpRequest request = SignUpRequest.builder()
			.authId("mju-graduate")
			.password("1q2w3e4r!")
			.studentNumber("60201000")
			.engLv("ENG12")
			.build();

		//when
		ResultActions actions = mockMvc.perform(
			post("/api/v1/users/sign-up")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
		);

		//then
		actions.
			andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("아이디는 6자리 이상 20자리 미민이어야한다.")
	@Test
	void 아아디_형식_오류() throws Exception {
		SignUpRequest request = SignUpRequest.builder()
			.authId("mju10")
			.password("1q2w3e4r!")
			.studentNumber("60201000")
			.engLv("ENG12")
			.build();

		//when //then
		mockMvc.perform(
				post("/api/v1/users/sign-up")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value("아이디는 6자에서 20자 사이여야합니다."));
	}

	@DisplayName("비밀번호는 8자리 이상 20자리 미민이어야한다.")
	@Test
	void 비밀번호_길이_오류() throws Exception {
		SignUpRequest request = SignUpRequest.builder()
			.authId("mju-graduate")
			.password("1q2w3e4r!1q2w3e4r!1q2w3e4r!")
			.studentNumber("60201000")
			.engLv("ENG12")
			.build();

		//when //then
		mockMvc.perform(
				post("/api/v1/users/sign-up")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value("비밀번호는 8자에서 20자 사이여야합니다."));
	}

	@DisplayName("비밀번호는 문자, 숫자, 기호의 조합이어야한다.")
	@Test
	void 비밀번호_형식_오류() throws Exception {
		SignUpRequest request = SignUpRequest.builder()
			.authId("mju-graduate")
			.password("1q2w3e4r")
			.studentNumber("60201000")
			.engLv("ENG12")
			.build();

		//when //then
		mockMvc.perform(
				post("/api/v1/users/sign-up")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value("비밀번호는 문자, 숫자, 기호가 1개 이상 포함되어야합니다."));
	}

	@DisplayName("로그인 아이디 중복 여부를 체크한다.")
	@Test
	void checkAuthIdDuplication() throws Exception {
		//given
		String authId = "testUserId";
		boolean notDuplicated = true;
		AuthIdDuplicationResponse authIdDuplicationResponse = AuthIdDuplicationResponse.builder()
			.authId(authId)
			.notDuplicated(notDuplicated).build();
		given(checkAuthIdDuplicationUseCase.checkAuthIdDuplication(authId)).willReturn(authIdDuplicationResponse);

		//when //then`
		mockMvc.perform(
				get("/api/v1/users/sign-up/check-duplicate-auth-id")
					.param("auth-id", authId)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authId").value(authId))
			.andExpect(jsonPath("$.notDuplicated").value(notDuplicated));
	}
}
