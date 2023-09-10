package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save;

import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save.SaveTakenLectureCommand;

public interface SaveTakenLectureFromParsingTextUseCase {

	void saveTakenLectures(SaveTakenLectureCommand saveTakenLectureCommand);
}
