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
public class TransferStudentGraduationStrategy implements StudentGraduationStrategy {

	@Override
	public StudentCategory getSupportedStudentCategory() {
		return StudentCategory.TRANSFER;
	}

	@Override
	public GraduationRequirement createGraduationRequirement(
		User user,
		DefaultGraduationRequirementType defaultType
	) {
		return defaultType.createTransferGraduationRequirement(user);
	}

	@Override
	public List<DetailGraduationResult> generateDetailGraduationResults(
		User user,
		TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement,
		DetailGraduationResultGenerator generator
	) {
		List<DetailGraduationResult> detailGraduationResults = new ArrayList<>();
		detailGraduationResults.add(
			generator.generateTransferChristian(user, takenLectureInventory, graduationRequirement)
		);
		detailGraduationResults.addAll(
			generator.generateMajor(user, takenLectureInventory, graduationRequirement)
		);
		return detailGraduationResults;
	}
}

