package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

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

	@Override
	public GraduationResult calculateGraduation(User user) {
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);
		TakenLectureInventory takenLectureInventory = findTakenLectureUseCase.findTakenLectures(user.getId());

		ChapelResult chapelResult = generateChapelResult(takenLectureInventory);
		List<DetailGraduationResult> detailGraduationResults = generateDetailGraduationResults(user,
			takenLectureInventory, graduationRequirement);

		GraduationResult graduationResult = generateGraduationResult(chapelResult, detailGraduationResults,
			takenLectureInventory, graduationRequirement);
		handleDuplicatedTakenCredit(user, graduationResult);
		updateUserGraduationInformation(user, graduationResult);
		return graduationResult;
	}

	private GraduationRequirement determineGraduationRequirement(User user) {
		College userCollage = College.findBelongingCollege(user.getPrimaryMajor());
		DefaultGraduationRequirementType defaultGraduationRequirement = DefaultGraduationRequirementType.determineGraduationRequirement(
			userCollage, user);
		return defaultGraduationRequirement.convertToProfitGraduationRequirement(user);
	}

	private ChapelResult generateChapelResult(TakenLectureInventory takenLectureInventory) {
		ChapelResult chapelResult = ChapelResult.create(takenLectureInventory);
		chapelResult.checkCompleted();
		return chapelResult;
	}

	private List<DetailGraduationResult> generateDetailGraduationResults(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		List<DetailGraduationResult> detailGraduationResults = new ArrayList<>(List.of(
			generateCommonCultureDetailGraduationResult(
				user, takenLectureInventory, graduationRequirement),
			generateCoreCultureDetailGraduationResult(
				user, takenLectureInventory, graduationRequirement)
		));
		detailGraduationResults.addAll(generateBasicAcademicalDetailGraduationResult(
			user, takenLectureInventory, graduationRequirement));
		detailGraduationResults.addAll(generateMajorDetailGraduationResult(
			user, takenLectureInventory, graduationRequirement));
		return detailGraduationResults;
	}

	private DetailGraduationResult generateCommonCultureDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		return calculateCommonCultureGraduationService.calculateSingleDetailGraduation(user,
			GraduationCategory.COMMON_CULTURE, takenLectureInventory, graduationRequirement);
	}

	private DetailGraduationResult generateCoreCultureDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		return calculateCoreCultureGraduationService.calculateSingleDetailGraduation(user,
			GraduationCategory.CORE_CULTURE, takenLectureInventory, graduationRequirement);
	}

	private List<DetailGraduationResult> generateBasicAcademicalDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		return calculateBasicAcademicalCultureGraduationService.calculateAllDetailGraduation(user, takenLectureInventory, graduationRequirement);
	}

	private List<DetailGraduationResult> generateMajorDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		return calculateMajorGraduationService.calculateAllDetailGraduation(user, takenLectureInventory, graduationRequirement);
	}

	private GraduationResult generateGraduationResult(ChapelResult chapelResult,
		List<DetailGraduationResult> detailGraduationResults, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		GraduationResult graduationResult = GraduationResult.create(chapelResult, detailGraduationResults);
		graduationResult.handleLeftTakenLectures(takenLectureInventory, graduationRequirement);
		graduationResult.checkGraduated(graduationRequirement);
		return graduationResult;
	}

	private void handleDuplicatedTakenCredit(User user, GraduationResult graduationResult) {
		if (user.getStudentCategory() == StudentCategory.DUAL_MAJOR) {
			int duplicatedBasicAcademicalCultureCredit =
				findBasicAcademicalCulturePort.findDuplicatedLecturesBetweenMajors(user).stream()
					.mapToInt(basicAcademicalCulture -> basicAcademicalCulture.getLecture().getCredit())
					.sum();
			graduationResult.deductDuplicatedCredit(duplicatedBasicAcademicalCultureCredit);
		}
	}

	private void updateUserGraduationInformation(User user, GraduationResult graduationResult) {
		UpdateStudentInformationCommand updateStudentInformationCommand = UpdateStudentInformationCommand.builder()
			.user(user)
			.name(user.getName())
			.studentCategory(user.getStudentCategory())
			.major(user.getPrimaryMajor())
			.dualMajor(user.getDualMajor())
			.subMajor(user.getSubMajor())
			.totalCredit(graduationResult.getTotalCredit())
			.takenCredit(graduationResult.getTakenCredit())
			.graduate(graduationResult.isGraduated())
			.build();
		updateStudentInformationUseCase.updateUser(updateStudentInformationCommand);
	}
}
