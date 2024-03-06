package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchLectureCommand {
	private final String type;
	private final String keyword;

	@Builder
	private SearchLectureCommand(String type, String keyword) {
		this.type = type;
		this.keyword = keyword;
	}

	public static SearchLectureCommand toCommand(String type, String keyword) {
		return SearchLectureCommand.builder()
			.type(type)
			.keyword(keyword)
			.build();
	}

}
