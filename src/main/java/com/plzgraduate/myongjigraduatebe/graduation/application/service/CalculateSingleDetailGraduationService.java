package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateSingleDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.StudentGraduationStrategyFactory;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateSingleDetailGraduationService implements
	CalculateSingleDetailGraduationUseCase {

	private final FindUserUseCase findUserUseCase;
	private final FindTakenLectureUseCase findTakenLectureUseCase;
	private final List<CalculateDetailGraduationUseCase> calculateDetailGraduationUseCases;
	private final StudentGraduationStrategyFactory strategyFactory;

	@Override
	public DetailGraduationResult calculateSingleDetailGraduation(Long userId,
		GraduationCategory graduationCategory) {
		User user = findUserUseCase.findUserById(userId);
		user.getStudentCategory()
			.validateGraduationCategoryInclusion(graduationCategory);
		TakenLectureInventory takenLectures = findTakenLectureUseCase.findTakenLectures(userId);
		CalculateDetailGraduationUseCase calculateDetailGraduationUseCase = determineCalculateDetailGraduationUseCase(
			graduationCategory);
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);

		return calculateDetailGraduationUseCase.calculateSingleDetailGraduation(user,
			graduationCategory, takenLectures,
			graduationRequirement);
	}

	private CalculateDetailGraduationUseCase determineCalculateDetailGraduationUseCase(
		GraduationCategory graduationCategory) {
		return calculateDetailGraduationUseCases.stream()
			.filter(calculateDetailGraduationUseCase -> calculateDetailGraduationUseCase.supports(
				graduationCategory))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("No calculate detail graduation case found"));
	}

	private GraduationRequirement determineGraduationRequirement(User user) {
		College userCollage = College.findBelongingCollege(user.getPrimaryMajor(), user.getEntryYear());
		DefaultGraduationRequirementType defaultGraduationRequirement = DefaultGraduationRequirementType.determineGraduationRequirement(
			userCollage, user);
		return strategyFactory.getStrategy(user.getStudentCategory())
			.createGraduationRequirement(user, defaultGraduationRequirement);
	}
}
