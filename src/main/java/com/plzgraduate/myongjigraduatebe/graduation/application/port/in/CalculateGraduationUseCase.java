package com.plzgraduate.myongjigraduatebe.graduation.application.port.in;

import com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response.GraduationResponse;

public interface CalculateGraduationUseCase {

	GraduationResponse calculateGraduation(Long userId);
}
