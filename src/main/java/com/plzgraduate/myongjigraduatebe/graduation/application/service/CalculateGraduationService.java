package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorGraduationCategory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCommonCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCoreCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDualBasicAcademicalCultureDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDualElectiveMajorDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDualMandatoryMajorDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculatePrimaryBasicAcademicalCultureDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculatePrimaryElectiveMajorDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculatePrimaryMandatoryMajorDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.submajor.SubMajorManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CalculateGraduationService implements CalculateGraduationUseCase {

	private final FindMajorPort findMajorPort;

	private final FindTakenLectureUseCase findTakenLectureUseCase;
	private final CalculateCommonCultureGraduationUseCase calculateCommonCultureGraduationUseCase;
	private final CalculateCoreCultureGraduationUseCase calculateCoreCultureGraduationUseCase;
	private final CalculatePrimaryMandatoryMajorDetailGraduationUseCase calculatePrimaryMandatoryMajorDetailGraduationUseCase;
	private final CalculatePrimaryElectiveMajorDetailGraduationUseCase calculatePrimaryElectiveMajorDetailGraduationUseCase;
	private final CalculatePrimaryBasicAcademicalCultureDetailGraduationService calculatePrimaryBasicAcademicalCultureDetailGraduationService;
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
				user, takenLectureInventory, graduationRequirement),
			generteBasicAcademicalDetailGraduationResult(
				user, takenLectureInventory, graduationRequirement)
		));
		detailGraduationResults.addAll(
			generatePrimaryMajorDetailGraduations(user, takenLectureInventory, graduationRequirement));

		addPrimaryMajorDetailGraduation(user, takenLectureInventory, graduationRequirement, detailGraduationResults);

		if (user.getStudentCategory() == StudentCategory.SUB_MAJOR) {
			detailGraduationResults.add(
				generateSubMajorDetailGraduationResult(user, takenLectureInventory, graduationRequirement));
		}

		return detailGraduationResults;
	}

	private DetailGraduationResult generateCommonCultureDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		return calculateCommonCultureGraduationUseCase.calculateDetailGraduation(user, takenLectureInventory,
			graduationRequirement);
	}

	private DetailGraduationResult generateCoreCultureDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		return calculateCoreCultureGraduationUseCase.calculateDetailGraduation(user, takenLectureInventory,
			graduationRequirement);
	}

	private DetailGraduationResult generteBasicAcademicalDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		return calculatePrimaryBasicAcademicalCultureDetailGraduationUseCase.calculateDetailGraduation(user,
			takenLectureInventory, graduationRequirement);
	}

	private List<DetailGraduationResult> generatePrimaryMajorDetailGraduations(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<MajorLecture> graduationPrimaryMajorLectures = findMajorPort.findMajor(user.getPrimaryMajor());

		MajorGraduationManager majorManager = new MajorManager();
		majorManager.designateMajorGraduationCategory(PRIMARY);
		DetailGraduationResult primaryMajorDetailGraduationResult = majorManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationPrimaryMajorLectures, graduationRequirement.getPrimaryMajorCredit());
		DetailGraduationResult primaryMandatoryMajorDetailGraduationResult = calculatePrimaryMandatoryMajorDetailGraduationUseCase.isolatePrimaryMandatoryMajorDetailGraduation(
			primaryMajorDetailGraduationResult);
		DetailGraduationResult primaryElectiveMajorDetailGraduationResult = calculatePrimaryElectiveMajorDetailGraduationUseCase.isolatePrimaryElectiveMajorDetailGraduation(
			primaryMajorDetailGraduationResult);

		detailGraduationResults.addAll(
			List.of(primaryMandatoryMajorDetailGraduationResult, primaryElectiveMajorDetailGraduationResult));
	}

	private DetailGraduationResult generateSubMajorDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<MajorLecture> graduationSubMajorLectures = findMajorPort.findMajor(user.getSubMajor());
		GraduationManager<MajorLecture> subMajorManager = new SubMajorManager();
		return subMajorManager.createDetailGraduationResult(user, takenLectureInventory, graduationSubMajorLectures,
			graduationRequirement.getSubMajorCredit());
	}

	private GraduationResult generateGraduationResult(ChapelResult chapelResult,
		List<DetailGraduationResult> detailGraduationResults, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		GraduationResult graduationResult = GraduationResult.create(chapelResult, detailGraduationResults);
		graduationResult.handleLeftTakenLectures(takenLectureInventory, graduationRequirement);
		graduationResult.checkGraduated();
		return graduationResult;
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
