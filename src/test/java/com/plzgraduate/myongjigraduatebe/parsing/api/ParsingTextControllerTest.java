package com.plzgraduate.myongjigraduatebe.parsing.api;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.parsing.api.dto.request.ParsingTextRequest;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class ParsingTextControllerTest extends WebAdaptorTestSupport {

	@SuppressWarnings("unused")
	@MockBean
	private TakenLectureCacheEvict takenLectureCacheEvict;

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

		then(parsingTextUseCase).should()
			.enrollParsingText(any(Long.class), any(String.class));
		then(parsingTextHistoryUseCase).should()
			.generateSucceedParsingTextHistory(any(Long.class), any(String.class));

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

		doThrow(new InvalidPdfException("")).when(parsingTextUseCase)
			.enrollParsingText(any(Long.class), any(String.class));

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

		then(parsingTextHistoryUseCase).should()
			.generateFailedParsingTextHistory(any(Long.class), any(String.class));
	}

	@DisplayName("올바른 API Key로 기존 실패 데이터 재분석을 요청하면 성공한다.")
	@Test
	void analyzeExistingFailuresWithValidApiKey() throws Exception {
		//given
		given(failureAnalysisService.analyzeExistingFailures()).willReturn(5);

		//when
		ResultActions actions = mockMvc.perform(
			post("/api/v1/parsing-text/analyze-existing-failures")
				.header("X-Admin-Key", "test-admin-key")
				.with(csrf())
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.analyzedCount").value(5));
	}

	@DisplayName("API Key가 없는 경우 기존 실패 데이터 재분석 요청이 401로 실패한다.")
	@Test
	void analyzeExistingFailuresWithoutApiKey() throws Exception {
		//when
		ResultActions actions = mockMvc.perform(
			post("/api/v1/parsing-text/analyze-existing-failures")
				.with(csrf())
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@DisplayName("잘못된 API Key로 요청하면 401로 실패한다.")
	@Test
	void analyzeExistingFailuresWithWrongApiKey() throws Exception {
		//when
		ResultActions actions = mockMvc.perform(
			post("/api/v1/parsing-text/analyze-existing-failures")
				.header("X-Admin-Key", "wrong-key")
				.with(csrf())
		);

		//then
		actions
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

}
