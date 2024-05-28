package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_ELECTIVE_MAJOR;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDualElectiveMajorDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@UseCase
public class CalculateDualElectiveMajorDetailGraduationService
	implements CalculateDualElectiveMajorDetailGraduationUseCase {
	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == DUAL_ELECTIVE_MAJOR;
	}

	@Override
	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		return null;
	}

	@Override
	public DetailGraduationResult isolateDualElectiveMajorDetailGraduation(
		DetailGraduationResult dualMajorDetailGraduationResult) {
		return null;
	}
}
