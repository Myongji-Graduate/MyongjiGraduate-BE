package com.plzgraduate.myongjigraduatebe.user.api.resetpassword;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.user.api.resetpassword.ResetPasswordRequest;
import com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.response.ValidateUserResponse;

class ResetPasswordControllerTest extends WebAdaptorTestSupport {

	@DisplayName("학번으로 유저 정보 조회 후 로그인 이아디와 일치하는지 확인한다.")
	@Test
	void validateUser() throws Exception {
		//given
		String studentNumber = "60191656";
		String authId = "testAuthId";
		ValidateUserResponse response = ValidateUserResponse.builder()
			.passedUserValidation(true).build();
		given(validateUserUseCase.validateUser(studentNumber, authId)).willReturn(response);

		//when //then
		mockMvc.perform(get("/api/v1/users/{student-number}/validate", studentNumber)
				.param("auth-id", authId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.passedUserValidation", is(true)));
	}

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
			.andExpect(jsonPath("$.message", is("비밀번호는 특수문자를 포함한 8자에서 20자 사이여야합니다.")));
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
			.andExpect(jsonPath("$.message", is("비밀번호는 특수문자를 포함한 8자에서 20자 사이여야합니다.")));
	}

}
