package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;

public interface CalculateGraduationUseCase {

	GraduationResult calculateGraduation(Long userId);
}
