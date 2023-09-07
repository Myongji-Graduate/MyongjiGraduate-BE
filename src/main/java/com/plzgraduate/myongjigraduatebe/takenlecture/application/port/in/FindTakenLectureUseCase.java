package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in;

public interface FindTakenLectureUseCase {
	FindTakenLectureResponse getTakenLectures(Long userId);
}
