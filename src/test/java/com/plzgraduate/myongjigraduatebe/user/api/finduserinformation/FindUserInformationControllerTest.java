package com.plzgraduate.myongjigraduatebe.user.api.finduserinformation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class FindUserInformationControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("로그인 한 회원 정보를 조회한다.")
	@Test
	void getUserInformation() throws Exception {
		//given
		Long userId = 1L;
		String studentNumber = "111111111";
		String studentName = "testUser";
		String major = "testMajor";

		User user = User.builder()
			.id(userId)
			.studentNumber(studentNumber)
			.name(studentName)
			.primaryMajor(major)
			.studentCategory(StudentCategory.NORMAL)
			.totalCredit(134)
			.takenCredit(130.0)
			.graduated(false).build();

		given(findUserInformationUseCase.findUserInformation(userId)).willReturn(user);

		//when //then
		mockMvc.perform(get("/api/v1/users/me"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.studentNumber").value(studentNumber))
			.andExpect(jsonPath("$.studentName").value(studentName))
			.andExpect(jsonPath("$.completeDivision").isArray())
			.andExpect(jsonPath("$.completeDivision[0].majorType").value("PRIMARY"))
			.andExpect(jsonPath("$.completeDivision[0].major").value(user.getPrimaryMajor()))
			.andExpect(jsonPath("$.totalCredit").value(134))
			.andExpect(jsonPath("$.takenCredit").value(130.0))
			.andExpect(jsonPath("$.graduated").value(false))
		;
	}

}
