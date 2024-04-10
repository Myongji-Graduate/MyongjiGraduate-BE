package com.plzgraduate.myongjigraduatebe.graduation.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCommonCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultDetailGraduationResolver implements DetailGraduationResolver {

	private final CalculateCommonCultureGraduationUseCase calculateCommonCultureGraduationUseCase;

	@Override
	public Map<CalculateDetailGraduationUseCase, Integer> resolveDetailGraduationUseCase(User user,
		GraduationCategory graduationCategory) {
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);
		HashMap<CalculateDetailGraduationUseCase, Integer> resolvedDetailGraduation = new HashMap<>();
		switch (graduationCategory) {
			case COMMON_CULTURE:
				resolvedDetailGraduation.put(calculateCommonCultureGraduationUseCase,
					graduationRequirement.getCommonCultureCredit());
				return resolvedDetailGraduation;
			default:
				return resolvedDetailGraduation;
		}
	}

	private GraduationRequirement determineGraduationRequirement(User user) {
		College userCollage = College.findBelongingCollege(user.getPrimaryMajor());
		DefaultGraduationRequirementType defaultGraduationRequirement = DefaultGraduationRequirementType.determineGraduationRequirement(
			userCollage, user);
		return defaultGraduationRequirement.convertToProfitGraduationRequirement(user);
	}
}
