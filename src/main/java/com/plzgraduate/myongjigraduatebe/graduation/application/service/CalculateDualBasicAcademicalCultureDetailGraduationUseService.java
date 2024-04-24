package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDualBasicAcademicalCultureDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@UseCase
public class CalculateDualBasicAcademicalCultureDetailGraduationUseService implements
	CalculateDualBasicAcademicalCultureDetailGraduationUseCase {
	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == DUAL_BASIC_ACADEMICAL_CULTURE;
	}

	@Override
	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		return null;
	}
}
