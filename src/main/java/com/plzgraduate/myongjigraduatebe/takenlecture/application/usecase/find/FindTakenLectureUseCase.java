package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureRead;

public interface FindTakenLectureUseCase {

	TakenLectureInventory findTakenLectures(Long userId);

    TakenLectureRead findTakenLecturesWithUser(Long userId);
}
