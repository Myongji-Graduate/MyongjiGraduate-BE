package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_MANDATORY_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorGraduationCategory.DUAL;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDualMandatoryMajorDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateDualMandatoryMajorDetailGraduationService
	implements CalculateDualMandatoryMajorDetailGraduationUseCase {

	private final FindMajorPort findMajorPort;

	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == DUAL_MANDATORY_MAJOR;
	}

	@Override
	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<MajorLecture> graduationMajorLectures = findMajorPort.findMajor(user.getDualMajor());
		GraduationManager<MajorLecture> majorGraduationManager = new MajorManager(DUAL);
		DetailGraduationResult majorDetailGraduationResult = majorGraduationManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationMajorLectures, graduationRequirement.getDualMajorCredit());

		DetailCategoryResult mandatoryMajorDetailCategoryResult = separateMandatoryMajor(majorDetailGraduationResult);
		return DetailGraduationResult.create(DUAL_MANDATORY_MAJOR,
			mandatoryMajorDetailCategoryResult.getTotalCredits(), List.of(mandatoryMajorDetailCategoryResult));
	}

	@Override
	public DetailGraduationResult isolateDualMandatoryMajorDetailGraduation(
		DetailGraduationResult dualMajorDetailGraduationResult) {
		DetailCategoryResult mandatoryMajorDetailCategoryResult = separateMandatoryMajor(
			dualMajorDetailGraduationResult);
		return DetailGraduationResult.create(DUAL_MANDATORY_MAJOR,
			mandatoryMajorDetailCategoryResult.getTotalCredits(), List.of(mandatoryMajorDetailCategoryResult));
	}

	private DetailCategoryResult separateMandatoryMajor(
		DetailGraduationResult majorDetailGraduationResult) {
		DetailCategoryResult dualMandatoryMajorDetailCategoryResult = majorDetailGraduationResult.getDetailCategory().stream()
			.filter(detailCategoryResult -> detailCategoryResult.getDetailCategoryName().equals("전공필수"))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Not Found DetailCategoryResult(전공 필수)"));
		dualMandatoryMajorDetailCategoryResult.assignDetailCategoryName("복수전공필수");
		return dualMandatoryMajorDetailCategoryResult;
	}
}
