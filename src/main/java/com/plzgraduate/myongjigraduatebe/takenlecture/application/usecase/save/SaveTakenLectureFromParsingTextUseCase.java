package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface SaveTakenLectureFromParsingTextUseCase {

	void saveTakenLectures(User user, List<TakenLectureInformation> takenLectureInformationList);
}
