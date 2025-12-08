package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SubMajorStudentGraduationStrategy implements StudentGraduationStrategy {

	@Override
	public StudentCategory getSupportedStudentCategory() {
		return StudentCategory.SUB_MAJOR;
	}

	@Override
	public GraduationRequirement createGraduationRequirement(
		User user,
		DefaultGraduationRequirementType defaultType
	) {
		return defaultType.createDefaultGraduationRequirement(user);
	}

	@Override
	public List<DetailGraduationResult> generateDetailGraduationResults(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement,
		DetailGraduationResultGenerator generator
	) {
		List<DetailGraduationResult> detailGraduationResults = new ArrayList<>();
		detailGraduationResults.addAll(List.of(
			generator.generateCommonCulture(user, takenLectureInventory, graduationRequirement),
			generator.generateCoreCulture(user, takenLectureInventory, graduationRequirement)
		));
		detailGraduationResults.addAll(
			generator.generateBasicAcademicalCulture(user, takenLectureInventory, graduationRequirement)
		);
		detailGraduationResults.addAll(
			generator.generateMajor(user, takenLectureInventory, graduationRequirement)
		);
		return detailGraduationResults;
	}
}

