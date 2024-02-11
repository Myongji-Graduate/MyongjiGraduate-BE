package com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search;

import java.util.List;

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

	public static SearchLectureResponse from(List<LectureResponse> lectures) {
		return SearchLectureResponse.builder()
			.lectures(lectures)
			.build();
	}
}
