package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BusinessBasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.DefaultBasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.SocialScienceBasicAcademicManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateBasicAcademicalCultureGraduationService implements CalculateDetailGraduationUseCase {

	private final FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;

	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == PRIMARY_BASIC_ACADEMICAL_CULTURE
			|| graduationCategory == DUAL_BASIC_ACADEMICAL_CULTURE;
	}
	public List<DetailGraduationResult> calculateAllDetailGraduation(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {

		if (user.getStudentCategory() == StudentCategory.DUAL_MAJOR) {
			TakenLectureInventory copiedTakenLectureForPrimaryBasicAcademicalCulture = takenLectureInventory.copy();
			TakenLectureInventory copiedTakenLectureForDualBasicAcademicalCulture = takenLectureInventory.copy();
			DetailGraduationResult primaryBasicAcademicalCultureDetailGraduationResult = calculateSingleDetailGraduation(
				user, PRIMARY_BASIC_ACADEMICAL_CULTURE, copiedTakenLectureForPrimaryBasicAcademicalCulture, graduationRequirement);
			DetailGraduationResult dualBasicAcademicalCultureDetailGraduationResult = calculateSingleDetailGraduation(
				user, DUAL_BASIC_ACADEMICAL_CULTURE, copiedTakenLectureForDualBasicAcademicalCulture, graduationRequirement);
			syncOriginalTakenLectureInventory(takenLectureInventory,
				primaryBasicAcademicalCultureDetailGraduationResult,
				dualBasicAcademicalCultureDetailGraduationResult);
			return List.of(primaryBasicAcademicalCultureDetailGraduationResult,
				dualBasicAcademicalCultureDetailGraduationResult);
		}
		DetailGraduationResult primaryBasicAcademicalCultureGraduationResult = calculateSingleDetailGraduation(
			user, PRIMARY_BASIC_ACADEMICAL_CULTURE, takenLectureInventory, graduationRequirement);
		return List.of(primaryBasicAcademicalCultureGraduationResult);
	}

	@Override
	public DetailGraduationResult calculateSingleDetailGraduation(User user, GraduationCategory graduationCategory, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		MajorType majorType = MajorType.from(graduationCategory);
		String userMajor = user.getMajorByMajorType(majorType);
		Set<BasicAcademicalCultureLecture> graduationBasicAcademicalCultureLectures = findBasicAcademicalCulturePort.findBasicAcademicalCulture(userMajor);
		GraduationManager<BasicAcademicalCultureLecture> basicAcademicalCultureGraduationManager = determineBasicAcademicalCultureGraduationManager(userMajor);
		DetailGraduationResult detailGraduationResult = basicAcademicalCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationBasicAcademicalCultureLectures,
			graduationRequirement.getBasicCreditByMajorType(majorType));
		detailGraduationResult.assignGraduationCategory(graduationCategory);
		return detailGraduationResult;
	}

	private GraduationManager<BasicAcademicalCultureLecture> determineBasicAcademicalCultureGraduationManager(
		String userMajor) {
		List<BasicAcademicalManager> basicAcademicalManagers = List.of(
			new BusinessBasicAcademicalManager(),
			new SocialScienceBasicAcademicManager());

		return basicAcademicalManagers.stream()
			.filter(basicAcademicalManager -> basicAcademicalManager.isSatisfied(userMajor))
			.findFirst()
			.orElse(new DefaultBasicAcademicalManager());
	}

	private void syncOriginalTakenLectureInventory(TakenLectureInventory originalTakenLectureInventory,
		DetailGraduationResult primaryBasicAcademicalCultureDetailGraduationResult,
		DetailGraduationResult dualBasicAcademicalCultureDetailGraduationResult) {
		List<Lecture> primaryBasicAcademicalCultureTakenLectures = primaryBasicAcademicalCultureDetailGraduationResult.getDetailCategory()
			.get(0)
			.getTakenLectures();
		List<Lecture> dualBasicAcademicalCultureTakenLectures = dualBasicAcademicalCultureDetailGraduationResult.getDetailCategory()
			.get(0)
			.getTakenLectures();
		Set<Lecture> basicAcademicalCultureTakenLectures = new HashSet<>();
		basicAcademicalCultureTakenLectures.addAll(primaryBasicAcademicalCultureTakenLectures);
		basicAcademicalCultureTakenLectures.addAll(dualBasicAcademicalCultureTakenLectures);
		originalTakenLectureInventory.handleFinishedLectures(basicAcademicalCultureTakenLectures);
	}
}
