package com.plzgraduate.myongjigraduatebe.takenlecture.application.port;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;

public interface SaveTakenLecturePort {

	void saveTakenLectures(List<TakenLecture> takenLectures);
}
