package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.support.resolver.CalculateDetailGraduationUseCaseResolver;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateSingleDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateSingleDetailGraduationService implements CalculateSingleDetailGraduationUseCase {

	private final FindUserUseCase findUserUseCase;
	private final FindTakenLectureUseCase findTakenLectureUseCase;
	private final CalculateDetailGraduationUseCaseResolver calculateDetailGraduationUseCaseResolver;

	@Override
	public DetailGraduationResult calculateSingleDetailGraduation(Long userId, GraduationCategory graduationCategory) {
		User user = findUserUseCase.findUserById(userId);
		TakenLectureInventory takenLectures = findTakenLectureUseCase.findTakenLectures(userId);
		CalculateDetailGraduationUseCase calculateDetailGraduationUseCase = calculateDetailGraduationUseCaseResolver.resolveCalculateDetailGraduationUseCase(
			graduationCategory);
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);

		return calculateDetailGraduationUseCase.calculateDetailGraduation(user, takenLectures, graduationRequirement);
	}

	private GraduationRequirement determineGraduationRequirement(User user) {
		College userCollage = College.findBelongingCollege(user.getPrimaryMajor());
		DefaultGraduationRequirementType defaultGraduationRequirement = DefaultGraduationRequirementType.determineGraduationRequirement(
			userCollage, user);
		return defaultGraduationRequirement.convertToProfitGraduationRequirement(user);
	}
}
