package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BasicAcademicalGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.DefaultBasicAcademicalGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateBasicAcademicalCultureGraduationService implements
	CalculateDetailGraduationUseCase {

	private final FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;
	private final List<BasicAcademicalGraduationManager> basicAcademicalGraduationManagers;

	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == PRIMARY_BASIC_ACADEMICAL_CULTURE
			|| graduationCategory == DUAL_BASIC_ACADEMICAL_CULTURE;
	}

	@Override
	public DetailGraduationResult calculateSingleDetailGraduation(
		User user,
		GraduationCategory graduationCategory,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement
	) {
		MajorType majorType = MajorType.from(graduationCategory);
		String userMajor = user.getMajorByMajorType(majorType);
		int entryYear = user.getEntryYear();
		Set<BasicAcademicalCultureLecture> graduationBasicAcademicalCultureLectures =
			findBasicAcademicalCulturePort.findBasicAcademicalCulture(userMajor,entryYear);
		GraduationManager<BasicAcademicalCultureLecture> basicAcademicalCultureGraduationManager =
			determineBasicAcademicalCultureGraduationManager(userMajor,entryYear);
		DetailGraduationResult detailGraduationResult = basicAcademicalCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationBasicAcademicalCultureLectures,
			graduationRequirement.getBasicCreditByMajorType(majorType)
		);
		detailGraduationResult.assignGraduationCategory(graduationCategory);
		return detailGraduationResult;
	}

	public List<DetailGraduationResult> calculateAllDetailGraduation(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement
	) {
		if (user.getStudentCategory() == StudentCategory.DUAL_MAJOR) {
			TakenLectureInventory copiedTakenLectureForPrimaryBasicAcademicalCulture = takenLectureInventory.copy();
			TakenLectureInventory copiedTakenLectureForDualBasicAcademicalCulture = takenLectureInventory.copy();
			DetailGraduationResult primaryBasicAcademicalCultureDetailGraduationResult = calculateSingleDetailGraduation(
				user, PRIMARY_BASIC_ACADEMICAL_CULTURE,
				copiedTakenLectureForPrimaryBasicAcademicalCulture,
				graduationRequirement
			);
			DetailGraduationResult dualBasicAcademicalCultureDetailGraduationResult = calculateSingleDetailGraduation(
				user, DUAL_BASIC_ACADEMICAL_CULTURE,
				copiedTakenLectureForDualBasicAcademicalCulture,
				graduationRequirement
			);

			addExchangeCreditsToDualBasicAcademicalCulture(user, dualBasicAcademicalCultureDetailGraduationResult);

			syncOriginalTakenLectureInventory(
				takenLectureInventory,
				primaryBasicAcademicalCultureDetailGraduationResult,
				dualBasicAcademicalCultureDetailGraduationResult
			);
			return List.of(
				primaryBasicAcademicalCultureDetailGraduationResult,
				dualBasicAcademicalCultureDetailGraduationResult
			);
		}
		DetailGraduationResult primaryBasicAcademicalCultureGraduationResult = calculateSingleDetailGraduation(
			user, PRIMARY_BASIC_ACADEMICAL_CULTURE, takenLectureInventory, graduationRequirement);
		return List.of(primaryBasicAcademicalCultureGraduationResult);
	}

	private void addExchangeCreditsToDualBasicAcademicalCulture(User user, DetailGraduationResult dualBasicAcademicalCultureDetailGraduationResult) {
		int additionalCredits = user.getExchangeCredit().getDualBasicAcademicalCulture();
		dualBasicAcademicalCultureDetailGraduationResult.addCredit(additionalCredits);
	}

	private GraduationManager<BasicAcademicalCultureLecture> determineBasicAcademicalCultureGraduationManager(
		String userMajor, int entryYear
	) {
		return basicAcademicalGraduationManagers.stream()
			.filter(basicAcademicalManager -> basicAcademicalManager.isSatisfied(userMajor, entryYear))
			.findFirst()
			.orElse(new DefaultBasicAcademicalGraduationManager());
	}

	private void syncOriginalTakenLectureInventory(
		TakenLectureInventory originalTakenLectureInventory,
		DetailGraduationResult primaryBasicAcademicalCultureDetailGraduationResult,
		DetailGraduationResult dualBasicAcademicalCultureDetailGraduationResult
	) {
		List<Lecture> primaryBasicAcademicalCultureTakenLectures =
			primaryBasicAcademicalCultureDetailGraduationResult.getDetailCategory()
				.get(0)
				.getTakenLectures();
		List<Lecture> dualBasicAcademicalCultureTakenLectures =
			dualBasicAcademicalCultureDetailGraduationResult.getDetailCategory()
				.get(0)
				.getTakenLectures();

		Set<Lecture> basicAcademicalCultureTakenLectures = new HashSet<>();
		basicAcademicalCultureTakenLectures.addAll(primaryBasicAcademicalCultureTakenLectures);
		basicAcademicalCultureTakenLectures.addAll(dualBasicAcademicalCultureTakenLectures);
		originalTakenLectureInventory.sync(basicAcademicalCultureTakenLectures);
	}
}
