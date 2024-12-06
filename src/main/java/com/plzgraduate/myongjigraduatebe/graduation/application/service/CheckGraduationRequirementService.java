package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CheckGraduationRequirementUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CheckGraduationRequirementService implements CheckGraduationRequirementUseCase {

	private final CalculateGraduationService calculateGraduationService;

	@Override
	public GraduationResult checkGraduationRequirement(
		User anonymous,
		TakenLectureInventory takenLectureInventory
	) {
		return null;
	}
}
