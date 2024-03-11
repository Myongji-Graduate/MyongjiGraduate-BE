package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.SearchLectureResponse;

public interface SearchLectureUseCase {
	SearchLectureResponse searchLectures(String type, String keyword);
}
