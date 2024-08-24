package com.plzgraduate.myongjigraduatebe.user.api.withdraw;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import com.plzgraduate.myongjigraduatebe.user.api.withdraw.dto.request.WithDrawRequest;

class WithDrawControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("유저의 회원 탈퇴 요청을 수행한다.")
	@Test
	void withDraw() throws Exception {
		//given
		WithDrawRequest request = WithDrawRequest.builder()
			.password("abcd1234!").build();

		//when
		ResultActions actions = mockMvc.perform(
			delete("/api/v1/users/me")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON));

		//then
		then(withDrawUserUseCase).should().withDraw(any(), any(String.class));
		actions.
			andDo(print())
			.andExpect(status().isOk());
	}
}
