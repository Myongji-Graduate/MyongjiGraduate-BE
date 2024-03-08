package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.GraduationResponse;

public interface CalculateGraduationUseCase {

	GraduationResponse calculateGraduation(Long userId);
}
