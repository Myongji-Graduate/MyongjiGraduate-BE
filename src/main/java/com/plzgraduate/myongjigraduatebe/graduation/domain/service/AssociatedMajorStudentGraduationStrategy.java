package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FusionMajorMembershipPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssociatedMajorStudentGraduationStrategy implements StudentGraduationStrategy {

	private final FusionMajorMembershipPort fusionMajorMembershipPort;

	@Override
	public StudentCategory getSupportedStudentCategory() {
		return StudentCategory.ASSOCIATED_MAJOR;
	}

	@Override
	public GraduationRequirement createGraduationRequirement(
		User user, DefaultGraduationRequirementType defaultType
	) {
		GraduationRequirement requirement =
			defaultType.createDefaultGraduationRequirement(user);
		requirement.modifyCreditForFusionMajor(
			fusionMajorMembershipPort.findRequiredPrimaryMajorCredit(
				user.getAssociatedMajor(), user.getEntryYear(), user.getPrimaryMajor()));
		return requirement;
	}

	@Override
	public List<DetailGraduationResult> generateDetailGraduationResults(
		User user,
		TakenLectureInventory inventory,
		GraduationRequirement requirement,
		DetailGraduationResultGenerator generator
	) {
		List<DetailGraduationResult> results = new ArrayList<>();
		results.add(generator.generateCommonCulture(user, inventory, requirement));
		results.add(generator.generateCoreCulture(user, inventory, requirement));
		results.addAll(generator.generateBasicAcademicalCulture(user, inventory, requirement));
		results.addAll(generator.generateMajor(user, inventory, requirement));
		results.addAll(generator.generateFusionMajor(user, inventory));
		return results;
	}
}
