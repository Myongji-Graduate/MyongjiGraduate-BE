package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FusionMajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssociatedMajorStudentGraduationStrategyTest {

	@Mock
	private FusionMajorMembershipPort membershipPort;

	@Mock
	private StudentGraduationStrategy.DetailGraduationResultGenerator generator;

	@InjectMocks
	private AssociatedMajorStudentGraduationStrategy strategy;

	@Test
	void createsRequirementUsingFusionPrimaryMajorCredit() {
		User user = user();
		given(membershipPort.findRequiredPrimaryMajorCredit(
			"휴먼복지행정전공", 21, "국어국문학과")).willReturn(42);

		GraduationRequirement requirement = strategy.createGraduationRequirement(
			user, DefaultGraduationRequirementType.HUMANITIES_18_99);

		assertThat(strategy.getSupportedStudentCategory())
			.isEqualTo(StudentCategory.ASSOCIATED_MAJOR);
		assertThat(requirement.getPrimaryMajorCredit()).isEqualTo(42);
		assertThat(requirement.getFreeElectiveCredit()).isEqualTo(45);
	}

	@Test
	void generatesAllDefaultAndFusionDetailResultsInOrder() {
		User user = user();
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of());
		GraduationRequirement requirement = GraduationRequirement.builder().build();
		DetailGraduationResult common = DetailGraduationResult.builder().totalCredit(1).build();
		DetailGraduationResult core = DetailGraduationResult.builder().totalCredit(2).build();
		DetailGraduationResult basic = DetailGraduationResult.builder().totalCredit(3).build();
		DetailGraduationResult major = DetailGraduationResult.builder().totalCredit(4).build();
		DetailGraduationResult fusion = DetailGraduationResult.builder().totalCredit(5).build();
		given(generator.generateCommonCulture(user, inventory, requirement)).willReturn(common);
		given(generator.generateCoreCulture(user, inventory, requirement)).willReturn(core);
		given(generator.generateBasicAcademicalCulture(user, inventory, requirement))
			.willReturn(List.of(basic));
		given(generator.generateMajor(user, inventory, requirement)).willReturn(List.of(major));
		given(generator.generateFusionMajor(user, inventory)).willReturn(List.of(fusion));

		List<DetailGraduationResult> results = strategy.generateDetailGraduationResults(
			user, inventory, requirement, generator);

		assertThat(results).containsExactly(common, core, basic, major, fusion);
	}

	private User user() {
		return User.builder()
			.primaryMajor("국어국문학과")
			.associatedMajor("휴먼복지행정전공")
			.entryYear(21)
			.studentCategory(StudentCategory.ASSOCIATED_MAJOR)
			.build();
	}
}
