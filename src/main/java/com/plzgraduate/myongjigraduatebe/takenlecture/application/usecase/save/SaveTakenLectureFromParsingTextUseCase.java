package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;

public interface SaveTakenLectureFromParsingTextUseCase {

	void saveTakenLectures(User user, List<TakenLectureInformation> takenLectureInformationList);
}
