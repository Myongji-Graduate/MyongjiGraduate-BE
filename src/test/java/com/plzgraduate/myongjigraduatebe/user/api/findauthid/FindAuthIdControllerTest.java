package com.plzgraduate.myongjigraduatebe.user.api.findauthid;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;

class FindAuthIdControllerTest extends WebAdaptorTestSupport {

	@DisplayName("학번으로 해당 학생의 아이디를 조회한다.")
	@Test
	void findUserAuthId() throws Exception {
		//given
		String studentNumber = "60191111";
		String encryptedAuthId = "test***";
		given(findUserAuthIdUseCase.findUserAuthId(studentNumber)).willReturn(encryptedAuthId);

		//when //then
		mockMvc.perform(get("/api/v1/users/{student-number}/auth-id", studentNumber))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authId", is(encryptedAuthId)))
			.andExpect(jsonPath("$.studentNumber", is(studentNumber)));
	}

	@DisplayName("해당 학번의 유저가 없을 에러 응답을 반환한다.")
	@Test
	void findUserAuthIdWithoutUser() throws Exception {
		//given
		String studentNumber = "60191111";
		given(findUserAuthIdUseCase.findUserAuthId(any())).willThrow(
			new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

		//when //then
		mockMvc.perform(get("/api/v1/users/{student-number}/auth-id", studentNumber))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status", is(400)))
			.andExpect(jsonPath("$.message", is("해당 사용자를 찾을 수 없습니다.")));
	}

}
