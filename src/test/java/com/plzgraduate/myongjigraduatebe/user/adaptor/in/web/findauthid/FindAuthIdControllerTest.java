package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.findauthid;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserAuthIdUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.UserAuthIdResponse;

@WebMvcTest(controllers = FindAuthIdController.class)
class FindAuthIdControllerTest extends WebAdaptorTestSupport {

	@MockBean
	private FindUserAuthIdUseCase findUserAuthIdUseCase;

	@DisplayName("학번으로 해당 학생의 아이디를 조회한다.")
	@Test
	void findUserAuthId() throws Exception {
	    //given
		String studentNumber = "60191111";
		String encryptedAuthId = "test***";
		UserAuthIdResponse userAuthIdResponse = UserAuthIdResponse.builder()
			.authId(encryptedAuthId)
			.studentNumber(studentNumber).build();
		given(findUserAuthIdUseCase.findUserAuthId(studentNumber)).willReturn(userAuthIdResponse);

		//when //then
		mockMvc.perform(get("/api/v1/users/student-number/{studentNumber}", studentNumber))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authId", is(encryptedAuthId)))
			.andExpect(jsonPath("$.studentNumber", is(studentNumber)));
	}

}
