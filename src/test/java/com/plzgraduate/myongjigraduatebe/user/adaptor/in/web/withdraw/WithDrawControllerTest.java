package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.withdraw;

import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;

class WithDrawControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("유저의 회원 탈퇴 요청을 수행한다.")
	@Test
	void withDraw() throws Exception {
	    //given
		Long userId = 1L;
	
	    //when
		ResultActions actions = mockMvc.perform(
			delete("/api/v1/users")
				.with(csrf()));
	
	    //then
		then(withDrawUserUseCase).should().withDraw(userId);
		actions.
			andDo(print())
			.andExpect(status().isOk());
	}
}
