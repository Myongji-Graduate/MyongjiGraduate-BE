package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.resetpassword;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.plzgraduate.myongjigraduatebe.user.application.port.in.resetpassword.ResetPasswordUseCase;

@WebMvcTest(controllers = ResetPasswordController.class)
class ResetPasswordControllerTest extends WebAdaptorTestSupport {

	@MockBean
	private ResetPasswordUseCase resetPasswordUseCase;

	@DisplayName("아이디로 유저 정보 조회 후 비밀번호를 변경한다.")
	@Test
	void resetPassword() throws Exception {
	    //given
		ResetPasswordRequest request = ResetPasswordRequest.builder()
			.authId("authId")
			.newPassword("abcd1234@")
			.passwordCheck("abcd1234@").build();

		//when
		ResultActions actions = mockMvc.perform(
			patch("/api/v1/users/password")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()));

		//then
		actions.
			andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("아이디가 없을 시 예외가 발생한다.")
	@Test
	void resetPasswordWithNonAuthId() throws Exception {
		//given
		ResetPasswordRequest request = ResetPasswordRequest.builder()
			.newPassword("abcd1234@")
			.passwordCheck("abcd1234@").build();

		//when
		ResultActions actions = mockMvc.perform(
			patch("/api/v1/users/password")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()));

		//then
		actions.
			andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message", is("아이디를 입력해주세요.")));
	}

	@DisplayName("새로운 비밀번호가 비밀번호 형식에 맞지 않을 시 예외가 발생한다.")
	@Test
	void resetPasswordWithUnSuitableNewPassword() throws Exception {
		//given
		ResetPasswordRequest request = ResetPasswordRequest.builder()
			.authId("authId")
			.newPassword("abcd1234")
			.passwordCheck("abcd1234@").build();

		//when
		ResultActions actions = mockMvc.perform(
			patch("/api/v1/users/password")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()));

		//then
		actions.
			andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message", is("비밀번호는 문자, 숫자, 기호가 1개 이상 포함되어야합니다.")));
	}

	@DisplayName("비밀번호 확인 비밀번호가 비밀번호 형식에 맞지 않을 시 예외가 발생한다.")
	@Test
	void resetPasswordWithUnSuitableCheckPassword() throws Exception {
		//given
		ResetPasswordRequest request = ResetPasswordRequest.builder()
			.authId("authId")
			.newPassword("abcd123@")
			.passwordCheck("abcd1234").build();

		//when
		ResultActions actions = mockMvc.perform(
			patch("/api/v1/users/password")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf()));

		//then
		actions.
			andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message", is("비밀번호는 문자, 숫자, 기호가 1개 이상 포함되어야합니다.")));
	}

}
