package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface CalculateDetailGraduationUseCase {

	boolean supports(GraduationCategory graduationCategory);

	DetailGraduationResult calculateSingleDetailGraduation(User user,
		GraduationCategory graduationCategory, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement);
}
