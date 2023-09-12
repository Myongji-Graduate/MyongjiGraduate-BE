package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.signin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.signin.SignInController;
import com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.signin.SignInRequest;
import com.plzgraduate.myongjigraduatebe.auth.application.port.signin.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;

@WebMvcTest(controllers = SignInController.class)
class SignInControllerTest extends WebAdaptorTestSupport {

	@MockBean
	private SignInUseCase signInUseCase;

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
				.with(csrf())
		)
			.andDo(print())
			.andExpect(status().isOk());
	}
}
