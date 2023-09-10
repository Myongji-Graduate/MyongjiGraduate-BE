package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find;

import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureResponse;

public interface FindTakenLectureUseCase {
	FindTakenLectureResponse getTakenLectures(Long userId);
}
