package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;

public interface StudentGraduationStrategy {

	/**
	 * 이 전략이 지원하는 학생 유형을 반환합니다.
	 */
	StudentCategory getSupportedStudentCategory();

	/**
	 * 학생 유형에 맞는 졸업 요건을 생성합니다.
	 */
	GraduationRequirement createGraduationRequirement(
		User user,
		DefaultGraduationRequirementType defaultType
	);

	/**
	 * 학생 유형에 맞는 상세 졸업 결과 목록을 생성합니다.
	 */
	List<DetailGraduationResult> generateDetailGraduationResults(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement,
		DetailGraduationResultGenerator generator
	);

	/**
	 * DetailGraduationResult 생성에 필요한 의존성을 주입하기 위한 인터페이스
	 */
	interface DetailGraduationResultGenerator {
		DetailGraduationResult generateCommonCulture(User user, TakenLectureInventory inventory,
			GraduationRequirement requirement);

		DetailGraduationResult generateCoreCulture(User user, TakenLectureInventory inventory,
			GraduationRequirement requirement);

		List<DetailGraduationResult> generateBasicAcademicalCulture(User user,
			TakenLectureInventory inventory, GraduationRequirement requirement);

		List<DetailGraduationResult> generateMajor(User user, TakenLectureInventory inventory,
			GraduationRequirement requirement);

		DetailGraduationResult generateTransferChristian(User user,
			TakenLectureInventory inventory, GraduationRequirement requirement);
	}
}

