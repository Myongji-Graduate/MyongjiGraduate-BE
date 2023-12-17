package com.plzgraduate.myongjigraduatebe.graduation.application.port.in;

import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response.GraduationResponse;

public interface CalculateGraduationUseCase {

	GraduationResponse calculateGraduation(Long userId);
}
