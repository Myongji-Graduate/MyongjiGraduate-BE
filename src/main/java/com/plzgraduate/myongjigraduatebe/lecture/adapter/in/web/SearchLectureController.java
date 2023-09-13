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

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/lectures")
@Validated
class SearchLectureController {

	private final SearchLectureUseCase searchLectureUseCase;

	@GetMapping
	public SearchLectureResponse searchLecture(
		@RequestParam(defaultValue = "name") String type,
		@RequestParam @Size(min = 2, message = "검색어를 2자리 이상 입력해주세요.") String keyword
	) {
		return searchLectureUseCase.searchLectures(SearchLectureCommand.toCommand(type, keyword));
	}
}
