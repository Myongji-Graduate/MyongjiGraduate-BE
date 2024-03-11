package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find;

import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.response.FindTakenLectureResponse;

public interface FindTakenLectureUseCase {
	FindTakenLectureResponse findTakenLectures(Long userId);
}
