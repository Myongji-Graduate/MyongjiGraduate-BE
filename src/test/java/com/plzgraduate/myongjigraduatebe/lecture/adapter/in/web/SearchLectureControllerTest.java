package com.plzgraduate.myongjigraduatebe.lecture.adapter.in.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.LectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.SearchLectureResponse;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;

class SearchLectureControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("type과 keyword를 통해 과목정보를 검색한다.")
	@Test
	void searchLecture() throws Exception {
		//given
		List<LectureResponse> searchLectures = List.of(
			LectureResponse.builder().id(1L).build()
		);
		SearchLectureResponse searchLectureResponse = SearchLectureResponse.builder().lectures(searchLectures).build();
		given(searchLectureUseCase.searchLectures(any())).willReturn(searchLectureResponse);

		//when //then
		mockMvc.perform(get("/api/v1/lectures")
				.param("type", "name")
				.param("keyword", "기초"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lectures", hasSize(1)));

		then(searchLectureUseCase).should(times(1)).searchLectures(any());
	}

	@WithMockAuthenticationUser
	@DisplayName("과목 키워드를 2글자 이하로 입력할 경우 예외가 발생한다.")
	@Test
	void searchWithKeywordSizeUnder2() throws Exception {

		//when //then
		mockMvc.perform(get("/api/v1/lectures")
				.param("type", "name")
				.param("keyword", ""))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("검색어를 2자리 이상 입력해주세요."));

	}
}
