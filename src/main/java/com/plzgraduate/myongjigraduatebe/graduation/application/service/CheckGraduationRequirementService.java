package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CheckGraduationRequirementUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
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

		GraduationRequirement graduationRequirement =
			calculateGraduationService.determineGraduationRequirement(anonymous);

		ChapelResult chapelResult =
			calculateGraduationService.generateChapelResult(takenLectureInventory);

		List<DetailGraduationResult> detailGraduationResults = calculateGraduationService.generateDetailGraduationResults(
			anonymous,
			takenLectureInventory,
			graduationRequirement
		);

		GraduationResult graduationResult = calculateGraduationService.generateGraduationResult(
			chapelResult,
			detailGraduationResults,
			takenLectureInventory,
			graduationRequirement
		);

		calculateGraduationService.handleDuplicatedTakenCredit(anonymous, graduationResult);

		return graduationResult;
	}
}
