package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;

public interface CalculateDualMandatoryMajorDetailGraduationUseCase extends CalculateDetailGraduationUseCase {

	DetailGraduationResult isolateDualMandatoryMajorDetailGraduation(
		DetailGraduationResult dualMajorDetailGraduationResult);
}