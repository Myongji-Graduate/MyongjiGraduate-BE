package com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchedLectureDto {

	private final boolean addable;
	private final Lecture lecture;

	@Builder
	private SearchedLectureDto(boolean addable, Lecture lecture) {
		this.addable = addable;
		this.lecture = lecture;
	}

	public static SearchedLectureDto of(boolean addable, Lecture lecture) {
		return SearchedLectureDto.builder()
			.addable(addable)
			.lecture(lecture).build();
	}
}
