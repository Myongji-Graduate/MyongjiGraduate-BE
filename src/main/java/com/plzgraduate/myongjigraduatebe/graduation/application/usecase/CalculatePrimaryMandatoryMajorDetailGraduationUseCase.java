package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;

public interface CalculatePrimaryMandatoryMajorDetailGraduationUseCase extends CalculateDetailGraduationUseCase {

	DetailGraduationResult isolatePrimaryMandatoryMajorDetailGraduation(
		DetailGraduationResult primaryMajorDetailGraduationResult);
}