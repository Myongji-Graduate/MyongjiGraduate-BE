package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.graduation.application.dto.ResolvedDetailGraduation;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCommonCultureGraduationUseCase;
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
	public ResolvedDetailGraduation resolveDetailGraduationUseCase(User user,
		GraduationCategory graduationCategory) {
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);
		//TODO: 추가 CalculateDetailGraduationUseCase 작성
		switch (graduationCategory) {
			case COMMON_CULTURE:
				return ResolvedDetailGraduation.builder()
					.calculateDetailGraduationUseCase(calculateCommonCultureGraduationUseCase)
					.graduationCategoryTotalCredit(graduationRequirement.getCommonCultureCredit()).build();

			default:
				return ResolvedDetailGraduation.builder().build();
		}
	}

	private GraduationRequirement determineGraduationRequirement(User user) {
		College userCollage = College.findBelongingCollege(user.getPrimaryMajor());
		DefaultGraduationRequirementType defaultGraduationRequirement = DefaultGraduationRequirementType.determineGraduationRequirement(
			userCollage, user);
		return defaultGraduationRequirement.convertToProfitGraduationRequirement(user);
	}
}
