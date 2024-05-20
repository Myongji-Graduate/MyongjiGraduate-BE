package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCommonCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCoreCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculatePrimaryElectiveMajorDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculatePrimaryMandatoryMajorDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BusinessBasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.DefaultBasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.SocialScienceBasicAcademicManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.submajor.SubMajorManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CalculateGraduationService implements CalculateGraduationUseCase {

	private final FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;
	private final FindMajorPort findMajorPort;
	private final FindTakenLectureUseCase findTakenLectureUseCase;

	private final CalculateCommonCultureGraduationUseCase calculateCommonCultureGraduationUseCase;
	private final CalculateCoreCultureGraduationUseCase calculateCoreCultureGraduationUseCase;
	private final CalculatePrimaryMandatoryMajorDetailGraduationUseCase calculatePrimaryMandatoryMajorDetailGraduationUseCase;
	private final CalculatePrimaryElectiveMajorDetailGraduationUseCase calculatePrimaryElectiveMajorDetailGraduationUseCase;

	@Override
	public GraduationResult calculateGraduation(User user) {
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);
		TakenLectureInventory takenLectureInventory = findTakenLectureUseCase.findTakenLectures(user.getId());

		ChapelResult chapelResult = generateChapelResult(takenLectureInventory);
		List<DetailGraduationResult> detailGraduationResults = generateDetailGraduationResults(user,
			takenLectureInventory, graduationRequirement);

		return generateGraduationResult(chapelResult, detailGraduationResults,
			takenLectureInventory, graduationRequirement);
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
		Set<BasicAcademicalCultureLecture> graduationBasicAcademicalCultureLectures = findBasicAcademicalCulturePort.findBasicAcademicalCulture(
			user);
		GraduationManager<BasicAcademicalCultureLecture> basicAcademicalCultureGraduationManager = determineBasicAcademicalCultureGraduationManager(
			user);
		return basicAcademicalCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationBasicAcademicalCultureLectures,
			graduationRequirement.getBasicAcademicalCredit());
	}

	private GraduationManager<BasicAcademicalCultureLecture> determineBasicAcademicalCultureGraduationManager(
		User user) {
		GraduationManager<BasicAcademicalCultureLecture> basicAcademicalCultureGraduationManager;
		switch (College.findBelongingCollege(user.getPrimaryMajor())) {
			case BUSINESS:
				basicAcademicalCultureGraduationManager = new BusinessBasicAcademicalManager();
				break;
			case SOCIAL_SCIENCE:
				basicAcademicalCultureGraduationManager = new SocialScienceBasicAcademicManager();
				break;
			default:
				basicAcademicalCultureGraduationManager = new DefaultBasicAcademicalManager();
				break;
		}
		return basicAcademicalCultureGraduationManager;
	}

	private void addPrimaryMajorDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement, List<DetailGraduationResult> detailGraduationResults) {
		Set<MajorLecture> graduationPrimaryMajorLectures = findMajorPort.findMajor(user.getPrimaryMajor());
		MajorManager majorManager = new MajorManager();

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
}
