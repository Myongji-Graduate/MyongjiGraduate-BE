package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.update;

import java.util.List;

import lombok.extern.java.Log;

public interface UpdateTakenLectureUseCase {

	void modifyTakenLecture(Long userId, List<Long> deletedTakenLectureIds, List<Long> addedTakenLectureIds);
}
