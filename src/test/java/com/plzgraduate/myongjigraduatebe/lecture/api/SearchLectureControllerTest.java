package com.plzgraduate.myongjigraduatebe.lecture.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.SearchedLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SearchLectureControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("type과 keyword를 통해 과목정보를 검색한다.")
	@Test
	void searchLecture() throws Exception {
		//given
		List<SearchedLectureDto> searchedLectures = List.of(
			SearchedLectureDto.of(true, Lecture.from("code")));
		given(searchLectureUseCase.searchLectures(anyLong(), any(), any())).willReturn(
			searchedLectures);

		//when //then
		mockMvc.perform(get("/api/v1/lectures")
				.param("type", "name")
				.param("keyword", "기초"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(searchedLectures.size())))
			.andExpect(jsonPath("$.[0].taken").value(true));

		then(searchLectureUseCase).should(times(1))
			.searchLectures(anyLong(), any(), any());
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
			.andExpect(jsonPath("$.errorCode").value("검색어를 2자리 이상 입력해주세요."));

	}
}
