package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchLectureResponse {

	private List<LectureResponse> lectures;

	@Builder
	private SearchLectureResponse(List<LectureResponse> lectures) {
		this.lectures = lectures;
	}

	public static SearchLectureResponse from(List<Lecture> lectures) {
		return SearchLectureResponse.builder()
			.lectures(lectures.stream().map(LectureResponse::of).collect(Collectors.toList()))
			.build();
	}
}
