package com.plzgraduate.myongjigraduatebe.user.api.signup;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.INVALIDATED_AUTHID_TYPE;
import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.INVALIDATED_PASSWORD_TYPE;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.parsing.api.TakenLectureCacheEvict;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.request.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.AuthIdDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.StudentNumberDuplicationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class SignUpControllerTest extends WebAdaptorTestSupport {

	@MockBean
	private TakenLectureCacheEvict takenLectureCacheEvict;

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
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(INVALIDATED_AUTHID_TYPE.toString()));
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
			.andExpect(jsonPath("$.errorCode").value(INVALIDATED_PASSWORD_TYPE.toString()));
	}

	@DisplayName("비밀번호는 하나 이상의 기호가 포함되어야한다.")
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
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(INVALIDATED_PASSWORD_TYPE.toString()));
	}

	@DisplayName("로그인 아이디 중복 여부를 체크한다.")
	@Test
	void checkAuthIdDuplication() throws Exception {
		//given
		String authId = "testUserId";
		boolean notDuplicated = true;
		AuthIdDuplicationResponse authIdDuplicationResponse = AuthIdDuplicationResponse.builder()
			.authId(authId)
			.notDuplicated(notDuplicated)
			.build();
		given(checkAuthIdDuplicationUseCase.checkAuthIdDuplication(authId)).willReturn(
			authIdDuplicationResponse);

		//when //then
		mockMvc.perform(
				get("/api/v1/users/sign-up/check-duplicate-auth-id")
					.param("auth-id", authId)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authId").value(authId))
			.andExpect(jsonPath("$.notDuplicated").value(notDuplicated));
	}

	@DisplayName("학번 중복 여부를 체크한다.")
	@Test
	void checkStudentNumberDuplication() throws Exception {
		//given
		String studentNumber = "60191656";
		boolean notDuplicated = true;
		StudentNumberDuplicationResponse studentNumberDuplicationResponse = StudentNumberDuplicationResponse.builder()
			.studentNumber(studentNumber)
			.notDuplicated(notDuplicated)
			.build();
		given(checkStudentNumberDuplicationUseCase.checkStudentNumberDuplication(
			studentNumber)).willReturn(
			studentNumberDuplicationResponse);

		//when //then
		mockMvc.perform(
				get("/api/v1/users/sign-up/check-duplicate-student-number")
					.param("student-number", studentNumber)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.studentNumber").value(studentNumber))
			.andExpect(jsonPath("$.notDuplicated").value(notDuplicated));
	}
}
