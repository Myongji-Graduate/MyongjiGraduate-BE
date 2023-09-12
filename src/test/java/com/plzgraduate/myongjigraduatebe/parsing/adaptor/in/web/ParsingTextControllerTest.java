package com.plzgraduate.myongjigraduatebe.parsing.adaptor.in.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextCommand;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextHistoryUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextUseCase;
import com.plzgraduate.myongjigraduatebe.security.WithMockAuthenticationUser;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;

@WebMvcTest(controllers = ParsingTextController.class)
class ParsingTextControllerTest extends WebAdaptorTestSupport {

	@MockBean
	private ParsingTextUseCase parsingTextUseCase;

	@MockBean
	private ParsingTextHistoryUseCase parsingTextHistoryUseCase;

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

		then(parsingTextUseCase).should().enrollParsingText(any(ParsingTextCommand.class));
		then(parsingTextHistoryUseCase).should().saveParsingTextHistoryIfSuccess(any(ParsingTextCommand.class));

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

		doThrow(new InvalidPdfException("")).when(parsingTextUseCase).enrollParsingText(any(ParsingTextCommand.class));

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
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message").value(""));;

		then(parsingTextHistoryUseCase).should().saveParsingTextHistoryIfFail(any(ParsingTextCommand.class));
	}

}
