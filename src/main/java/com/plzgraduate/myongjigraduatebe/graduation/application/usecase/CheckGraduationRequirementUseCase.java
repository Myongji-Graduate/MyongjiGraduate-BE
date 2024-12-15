package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface CheckGraduationRequirementUseCase {

	GraduationResult checkGraduationRequirement(
		User user,
		TakenLectureInventory takenLectureInventory
	);
}
