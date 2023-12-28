package com.plzgraduate.myongjigraduatebe.lecture.adapter.in.web;

import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search.SearchLectureCommand;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search.SearchLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search.SearchLectureUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/lectures")
@Validated
@Tag(name = "SearchLecture", description = "type과 keyword를 통해 과목정보를 검색하는 API")
public class SearchLectureController {

	private final SearchLectureUseCase searchLectureUseCase;

	@GetMapping
	public SearchLectureResponse searchLecture(
		@Parameter(name = "type", description = "과목명 또는 과목코드") @RequestParam(defaultValue = "name") String type,
		@Parameter(name = "keyword", description = "검색어 2자리 이상") @RequestParam @Size(min = 2, message = "검색어를 2자리 이상 입력해주세요.") String keyword
	) {
		return searchLectureUseCase.searchLectures(SearchLectureCommand.toCommand(type, keyword));
	}
}
