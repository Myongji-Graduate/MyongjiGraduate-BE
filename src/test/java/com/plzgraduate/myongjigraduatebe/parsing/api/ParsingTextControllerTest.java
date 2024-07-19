package com.plzgraduate.myongjigraduatebe.parsing.api;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.parsing.api.dto.request.ParsingTextRequest;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;

class ParsingTextControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("파싱 텍스트를 등록한다.")
	@Test
	void enrollParsingText() throws Exception {
	    //given
		String parsingText = "parsingText";
		ParsingTextRequest request = ParsingTextRequest.builder()
			.parsingText(parsingText)
			.build();

		//when
		ResultActions actions = mockMvc.perform(
			post("/api/v1/parsing-text")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
		);

	    //then
		actions
			.andDo(print())
			.andExpect(status().isOk());

		then(parsingTextUseCase).should().enrollParsingText(any(Long.class), any(String.class));
		then(parsingTextHistoryUseCase).should().generateSucceedParsingTextHistory(any(Long.class), any(String.class));

	}

	@WithMockAuthenticationUser
	@DisplayName("예외가 발생하면 파싱 히스토리 실패메서드가 호출된다.")
	@Test
	void failToEnrollParsingText() throws Exception {
		//given
		String parsingText = "parsingText";
		ParsingTextRequest request = ParsingTextRequest.builder()
			.parsingText(parsingText)
			.build();

		doThrow(new InvalidPdfException("")).when(parsingTextUseCase).enrollParsingText(any(Long.class), any(String.class));

		//when
		ResultActions actions = mockMvc.perform(
			post("/api/v1/parsing-text")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(""));

		then(parsingTextHistoryUseCase).should().generateFailedParsingTextHistory(any(Long.class), any(String.class));
	}

}
