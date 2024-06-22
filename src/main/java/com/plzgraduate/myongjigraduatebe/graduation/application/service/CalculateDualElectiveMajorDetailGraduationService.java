package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType.DUAL;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDualElectiveMajorDetailGraduationUseCase;
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
public class CalculateDualElectiveMajorDetailGraduationService {

	private final FindMajorPort findMajorPort;

	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == DUAL_ELECTIVE_MAJOR;
	}

	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<MajorLecture> graduationMajorLectures = findMajorPort.findMajor(user.getDualMajor());
		GraduationManager<MajorLecture> majorGraduationManager = new MajorManager(DUAL);
		DetailGraduationResult majorDetailGraduationResult = majorGraduationManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationMajorLectures, graduationRequirement.getDualMajorCredit());

		DetailCategoryResult electiveMajorDetailCategoryResult = separateElectiveMajor(majorDetailGraduationResult);
		return DetailGraduationResult.create(DUAL_ELECTIVE_MAJOR, electiveMajorDetailCategoryResult.getTotalCredits(),
			List.of(electiveMajorDetailCategoryResult));
	}

	public DetailGraduationResult isolateDualElectiveMajorDetailGraduation(
		DetailGraduationResult dualMajorDetailGraduationResult) {
		DetailCategoryResult electiveMajorDetailCategoryResult = separateElectiveMajor(
			dualMajorDetailGraduationResult);
		return DetailGraduationResult.create(DUAL_ELECTIVE_MAJOR,
			electiveMajorDetailCategoryResult.getTotalCredits(), List.of(electiveMajorDetailCategoryResult));
	}

	private DetailCategoryResult separateElectiveMajor(
		DetailGraduationResult majorDetailGraduationResult) {
			DetailCategoryResult dualElectiveMajorDetailCategoryResult = majorDetailGraduationResult.getDetailCategory().stream()
				.filter(detailCategoryResult -> detailCategoryResult.getDetailCategoryName().equals("전공선택"))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Not Found DetailCategoryResult(전공 선택)"));
			dualElectiveMajorDetailCategoryResult.assignDetailCategoryName("복수전공선택");
			return dualElectiveMajorDetailCategoryResult;
	}
}
