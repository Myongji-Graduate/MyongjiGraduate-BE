package com.plzgraduate.myongjigraduatebe.lecture.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.SearchLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.SearchLectureUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.SearchedLectureDto;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/lectures")
@Validated
public class SearchLectureController implements SearchLectureApiPresentation {

	private final SearchLectureUseCase searchLectureUseCase;

	@GetMapping
	public List<SearchLectureResponse> searchLecture(
		@LoginUser Long userId,
		@RequestParam(defaultValue = "name") String type,
		@RequestParam @Size(min = 2, message = "검색어를 2자리 이상 입력해주세요.") String keyword
	) {
		return searchLectureUseCase.searchLectures(userId, type, keyword).stream()
			.map(SearchLectureResponse::from)
			.collect(Collectors.toList());
	}
}
