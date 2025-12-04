package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.StudentGraduationStrategy;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.StudentGraduationStrategyFactory;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
class CalculateGraduationService implements CalculateGraduationUseCase {

	private final FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;
	private final FindTakenLectureUseCase findTakenLectureUseCase;
	private final CalculateCommonCultureGraduationService calculateCommonCultureGraduationService;
	private final CalculateCoreCultureGraduationService calculateCoreCultureGraduationService;
	private final CalculateBasicAcademicalCultureGraduationService calculateBasicAcademicalCultureGraduationService;
	private final CalculateMajorGraduationService calculateMajorGraduationService;
	private final UpdateStudentInformationUseCase updateStudentInformationUseCase;
	private final StudentGraduationStrategyFactory strategyFactory;


	@Override
	public GraduationResult calculateGraduation(User user) {
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);
		TakenLectureInventory takenLectureInventory = findTakenLectureUseCase.findTakenLectures(
			user.getId()
		);
		List<DetailGraduationResult> detailGraduationResults = generateDetailGraduationResults(
			user,
			takenLectureInventory,
			graduationRequirement
		);
		ChapelResult chapelResult = generateChapelResult(user, takenLectureInventory);
		GraduationResult graduationResult = generateGraduationResult(
			chapelResult,
			detailGraduationResults,
			takenLectureInventory,
			graduationRequirement,
			user
		);
		handleDuplicatedTakenCredit(user, graduationResult);
		updateUserGraduationInformation(user, graduationResult);
		return graduationResult;
	}

	DetailGraduationResult generateTransferChristianDetailGraduationResult(
		User user,
		GraduationRequirement graduationRequirement,
		TakenLectureInventory takenLectureInventory
	) {
		double christianCreditRequirement = graduationRequirement.getChristianCredit();
		double totalTakenCredits = calculateChristianTakenCredits(user, takenLectureInventory) +
			user.getTransferCredit().getChristianLecture();

		if (totalTakenCredits > christianCreditRequirement) {
			totalTakenCredits = christianCreditRequirement;
		}

		return DetailGraduationResult.create(
			GraduationCategory.TRANSFER_CHRISTIAN,
			graduationRequirement.getChristianCredit(),
			List.of(DetailCategoryResult.builder()
				.takenCredits((int) totalTakenCredits)
				.isCompleted(totalTakenCredits >= graduationRequirement.getChristianCredit())
				.build()
			)
		);
	}

	private double calculateChristianTakenCredits(
		User user,
		TakenLectureInventory takenLectureInventory
	) {
		if (!user.isAnonymous()) {
			takenLectureInventory = findTakenLectureUseCase.findTakenLectures(user.getId());
		}
		return takenLectureInventory.calculateChristianCredits();

	}

	public GraduationRequirement determineGraduationRequirement(User user) {
		College userCollage = College.findBelongingCollege(user.getPrimaryMajor(), user.getEntryYear());
		DefaultGraduationRequirementType defaultGraduationRequirement =
			DefaultGraduationRequirementType.determineGraduationRequirement(userCollage, user);
		StudentGraduationStrategy strategy = strategyFactory.getStrategy(user.getStudentCategory());
		return strategy.createGraduationRequirement(user, defaultGraduationRequirement);
	}

	public ChapelResult generateChapelResult(
		User user,
		TakenLectureInventory takenLectureInventory
	) {
		if (user.isChapleReplaced()) {
			return ChapelResult.replaced();
		}
		ChapelResult chapelResult = ChapelResult.create(takenLectureInventory);
		chapelResult.checkCompleted();
		return chapelResult;
	}

	List<DetailGraduationResult> generateDetailGraduationResults(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement
	) {
		StudentGraduationStrategy strategy = strategyFactory.getStrategy(user.getStudentCategory());
		StudentGraduationStrategy.DetailGraduationResultGenerator generator =
			new StudentGraduationStrategy.DetailGraduationResultGenerator() {
				@Override
				public DetailGraduationResult generateCommonCulture(User user,
					TakenLectureInventory inventory, GraduationRequirement requirement) {
					return generateCommonCultureDetailGraduationResult(user, inventory, requirement);
				}

				@Override
				public DetailGraduationResult generateCoreCulture(User user,
					TakenLectureInventory inventory, GraduationRequirement requirement) {
					return generateCoreCultureDetailGraduationResult(user, inventory, requirement);
				}

				@Override
				public List<DetailGraduationResult> generateBasicAcademicalCulture(User user,
					TakenLectureInventory inventory, GraduationRequirement requirement) {
					return generateBasicAcademicalDetailGraduationResult(user, inventory, requirement);
				}

				@Override
				public List<DetailGraduationResult> generateMajor(User user,
					TakenLectureInventory inventory, GraduationRequirement requirement) {
					return generateMajorDetailGraduationResult(user, inventory, requirement);
				}

				@Override
				public DetailGraduationResult generateTransferChristian(User user,
					TakenLectureInventory inventory, GraduationRequirement requirement) {
					return generateTransferChristianDetailGraduationResult(user, requirement, inventory);
				}
			};

		return strategy.generateDetailGraduationResults(
			user, takenLectureInventory, graduationRequirement, generator
		);
	}

	private DetailGraduationResult generateCommonCultureDetailGraduationResult(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement
	) {
		return calculateCommonCultureGraduationService.calculateSingleDetailGraduation(
			user, GraduationCategory.COMMON_CULTURE, takenLectureInventory, graduationRequirement
		);
	}

	private DetailGraduationResult generateCoreCultureDetailGraduationResult(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement
	) {
		return calculateCoreCultureGraduationService.calculateSingleDetailGraduation(
			user, GraduationCategory.CORE_CULTURE, takenLectureInventory, graduationRequirement
		);
	}

	private List<DetailGraduationResult> generateBasicAcademicalDetailGraduationResult(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement
	) {
		return calculateBasicAcademicalCultureGraduationService.calculateAllDetailGraduation(
			user, takenLectureInventory, graduationRequirement
		);
	}

	private List<DetailGraduationResult> generateMajorDetailGraduationResult(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement
	) {
		return calculateMajorGraduationService.calculateAllDetailGraduation(
			user,
			takenLectureInventory,
			graduationRequirement
		);
	}

	GraduationResult generateGraduationResult(
		ChapelResult chapelResult,
		List<DetailGraduationResult> detailGraduationResults,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement,
		User user
	) {
		GraduationResult graduationResult = GraduationResult.create(
			chapelResult,
			detailGraduationResults
		);
		graduationResult.handleLeftTakenLectures(
			takenLectureInventory,
			graduationRequirement,
			user
		);
		graduationResult.checkGraduated(graduationRequirement, user);
		return graduationResult;
	}

	void handleDuplicatedTakenCredit(User user, GraduationResult graduationResult) {
		if (user.getStudentCategory() == StudentCategory.DUAL_MAJOR) {
			int duplicatedBasicAcademicalCultureCredit =
				findBasicAcademicalCulturePort.findDuplicatedLecturesBetweenMajors(user)
					.stream()
					.mapToInt(basicAcademicalCulture -> basicAcademicalCulture.getLecture()
						.getCredit())
					.sum();
			graduationResult.deductDuplicatedCredit(duplicatedBasicAcademicalCultureCredit);
		}
	}

	private void updateUserGraduationInformation(User user, GraduationResult graduationResult) {
		UpdateStudentInformationCommand updateStudentInformationCommand = UpdateStudentInformationCommand.update(
			user,
			graduationResult
		);
		updateStudentInformationUseCase.updateUser(updateStudentInformationCommand);
	}
}
