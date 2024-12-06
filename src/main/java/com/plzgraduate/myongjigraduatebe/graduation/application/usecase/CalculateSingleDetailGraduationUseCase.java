package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

public interface CalculateSingleDetailGraduationUseCase {

ㄱ	DetailGraduationResult calculateSingleDetailGraduation(
		Long userId,
		GraduationCategory graduationCategory
	);
}
