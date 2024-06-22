package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_MANDATORY_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.SUB_MAJOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.submajor.SubMajorManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateMajorGraduationService {

	private final FindMajorPort findMajorPort;

	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == PRIMARY_MANDATORY_MAJOR
			|| graduationCategory == PRIMARY_ELECTIVE_MAJOR
			|| graduationCategory == DUAL_MANDATORY_MAJOR
			|| graduationCategory == DUAL_ELECTIVE_MAJOR
			|| graduationCategory == SUB_MAJOR;
	}

	public List<DetailGraduationResult> calculateAllDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {

		DetailGraduationResult primaryMajorDetailGraduationResult = generateMajorDetailGraduationResult(user,
			MajorType.PRIMARY, takenLectureInventory, graduationRequirement);
		DetailGraduationResult primaryMandatoryMajorDetailGraduationResult = isolateMandatoryMajorDetailGraduation(
			PRIMARY_MANDATORY_MAJOR, primaryMajorDetailGraduationResult);
		DetailGraduationResult primaryElectiveMajorDetailGraduationResult = isolateElectiveMajorDetailGraduation(
			PRIMARY_ELECTIVE_MAJOR, primaryMajorDetailGraduationResult);

		List<DetailGraduationResult> majorGraduationResults = new ArrayList<>(
			List.of(primaryMandatoryMajorDetailGraduationResult, primaryElectiveMajorDetailGraduationResult));

		if (user.getStudentCategory() == StudentCategory.DUAL_MAJOR) {
			DetailGraduationResult dualMajorDetailGraduationResult = generateMajorDetailGraduationResult(user,
				MajorType.DUAL, takenLectureInventory, graduationRequirement);
			DetailGraduationResult dualMandatoryMajorDetailGraduationResult = isolateMandatoryMajorDetailGraduation(
				DUAL_MANDATORY_MAJOR, dualMajorDetailGraduationResult);
			DetailGraduationResult dualElectiveMajorDetailGraduationResult = isolateElectiveMajorDetailGraduation(
				DUAL_ELECTIVE_MAJOR, dualMajorDetailGraduationResult);
			majorGraduationResults.addAll(
				List.of(dualMandatoryMajorDetailGraduationResult, dualElectiveMajorDetailGraduationResult));
		}
		if (user.getStudentCategory() == StudentCategory.SUB_MAJOR) {
			majorGraduationResults.add(generateSubMajorDetailGraduationResult(user, takenLectureInventory, graduationRequirement));
		}
		return majorGraduationResults;
	}

	public DetailGraduationResult calculateSingleDetailGraduation(User user, GraduationCategory graduationCategory, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		MajorType majorType = MajorType.from(graduationCategory);

		if(majorType == MajorType.SUB) {
			return generateSubMajorDetailGraduationResult(user, takenLectureInventory, graduationRequirement);
		}

		DetailGraduationResult majorDetailGraduationResult = generateMajorDetailGraduationResult(user,
			majorType, takenLectureInventory, graduationRequirement);

		boolean isMandatory = graduationCategory.checkMandatoryIfSeperatedByMandatoryAndElective();

		if(isMandatory) {
			DetailCategoryResult majorDetailCategoryResult = separateMandatoryMajor(majorDetailGraduationResult);
			return DetailGraduationResult.create(graduationCategory,
				majorDetailCategoryResult.getTotalCredits(), List.of(majorDetailCategoryResult));
		}

		DetailCategoryResult majorDetailCategoryResult = separateElectiveMajor(majorDetailGraduationResult);
		return DetailGraduationResult.create(graduationCategory,
				majorDetailCategoryResult.getTotalCredits(), List.of(majorDetailCategoryResult));
	}

	private DetailGraduationResult generateMajorDetailGraduationResult(User user, MajorType majorType, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<MajorLecture> graduationMajorLectures = findMajorPort.findMajor(user.getMajorByMajorType(majorType));
		GraduationManager<MajorLecture> majorManager = new MajorManager(majorType);
		return majorManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationMajorLectures, graduationRequirement.getMajorCreditByMajorType(majorType));
	}

	private DetailGraduationResult generateSubMajorDetailGraduationResult(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<MajorLecture> graduationSubMajorLectures = findMajorPort.findMajor(user.getSubMajor());
		GraduationManager<MajorLecture> subMajorManager = new SubMajorManager();
		return subMajorManager.createDetailGraduationResult(user, takenLectureInventory, graduationSubMajorLectures,
			graduationRequirement.getSubMajorCredit());
	}

	private DetailGraduationResult isolateMandatoryMajorDetailGraduation(
		GraduationCategory graduationCategory, DetailGraduationResult primaryMajorDetailGraduationResult) {
		DetailCategoryResult mandatoryMajorDetailCategoryResult = separateMandatoryMajor(
			primaryMajorDetailGraduationResult);
		return DetailGraduationResult.create(graduationCategory,
			mandatoryMajorDetailCategoryResult.getTotalCredits(), List.of(mandatoryMajorDetailCategoryResult));
	}

	private DetailGraduationResult isolateElectiveMajorDetailGraduation(
		GraduationCategory graduationCategory, DetailGraduationResult detailPrimaryMajorGraduationResult) {
		DetailCategoryResult electiveMajorDetailCategoryResult = separateElectiveMajor(
			detailPrimaryMajorGraduationResult);
		return DetailGraduationResult.create(graduationCategory,
			electiveMajorDetailCategoryResult.getTotalCredits(), List.of(electiveMajorDetailCategoryResult));
	}

	private DetailCategoryResult separateMandatoryMajor(
		DetailGraduationResult majorDetailGraduationResult) {
		DetailCategoryResult primaryMandatoryMajorDetailCategoryResult = majorDetailGraduationResult.getDetailCategory().stream()
			.filter(detailCategoryResult -> detailCategoryResult.getDetailCategoryName().equals("전공필수"))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Not Found DetailCategoryResult(전공 필수)"));
		primaryMandatoryMajorDetailCategoryResult.assignDetailCategoryName("주전공필수");
		return primaryMandatoryMajorDetailCategoryResult;
	}

	private DetailCategoryResult separateElectiveMajor(
		DetailGraduationResult majorDetailGraduationResult) {
		DetailCategoryResult primaryElectiveDetailCategoryResult = majorDetailGraduationResult.getDetailCategory()
			.stream()
			.filter(detailCategoryResult -> detailCategoryResult.getDetailCategoryName().equals("전공선택"))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Not Found DetailCategoryResult(전공 선택)"));
		primaryElectiveDetailCategoryResult.assignDetailCategoryName("주전공선택");
		return primaryElectiveDetailCategoryResult;
	}
}