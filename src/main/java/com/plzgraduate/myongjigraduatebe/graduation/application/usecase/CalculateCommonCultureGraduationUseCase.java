package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface CalculateCommonCultureGraduationUseCase {

	DetailGraduationResult calculateCommonCulture(User user, TakenLectureInventory takenLectureInventory);
}
