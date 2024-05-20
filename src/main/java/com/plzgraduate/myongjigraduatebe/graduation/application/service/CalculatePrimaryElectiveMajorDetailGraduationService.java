package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculatePrimaryElectiveMajorDetailGraduationUseCase;
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
public class CalculatePrimaryElectiveMajorDetailGraduationService
	implements CalculatePrimaryElectiveMajorDetailGraduationUseCase {

	private final FindMajorPort findMajorPort;

	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == PRIMARY_ELECTIVE_MAJOR;
	}

	@Override
	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<MajorLecture> graduationMajorLectures = findMajorPort.findMajor(user.getPrimaryMajor());
		GraduationManager<MajorLecture> majorGraduationManager = new MajorManager();
		DetailGraduationResult majorDetailGraduationResult = majorGraduationManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationMajorLectures, graduationRequirement.getPrimaryMajorCredit());

		DetailCategoryResult electiveMajorDetailCategoryResult = separateElectiveMajor(majorDetailGraduationResult);

		return DetailGraduationResult.create(PRIMARY_ELECTIVE_MAJOR,
			electiveMajorDetailCategoryResult.getTotalCredits(), List.of(electiveMajorDetailCategoryResult));
	}

	@Override
	public DetailGraduationResult isolatePrimaryElectiveMajorDetailGraduation(
		DetailGraduationResult detailPrimaryMajorGraduationResult) {
		DetailCategoryResult electiveMajorDetailCategoryResult = separateElectiveMajor(
			detailPrimaryMajorGraduationResult);
		return DetailGraduationResult.create(PRIMARY_ELECTIVE_MAJOR,
			electiveMajorDetailCategoryResult.getTotalCredits(), List.of(electiveMajorDetailCategoryResult));
	}

	private DetailCategoryResult separateElectiveMajor(
		DetailGraduationResult majorDetailGraduationResult) {
		return majorDetailGraduationResult.getDetailCategory()
			.stream()
			.filter(detailCategoryResult -> detailCategoryResult.getDetailCategoryName().equals("전공선택"))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Not Found DetailCategoryResult(전공 선택)"));
	}
}
