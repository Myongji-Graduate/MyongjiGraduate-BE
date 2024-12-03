package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

public interface FindTakenLectureUseCase {

	TakenLectureInventory findTakenLectures(Long userId);
}
