package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.SearchedLectureDto;

public interface SearchLectureUseCase {
	List<SearchedLectureDto> searchLectures(Long userId, String type, String keyword);
}
