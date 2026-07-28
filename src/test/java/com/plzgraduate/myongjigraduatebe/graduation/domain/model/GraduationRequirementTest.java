package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.DualMajorGraduationRequirementType.BUSINESS_16_24;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.DualMajorGraduationRequirementType.ICT;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GraduationRequirementTest {

	@DisplayName("공통교양의 영어카테고리 학점(6점)을 일반교양 학점으로 이관한다.")
	@Test
	void transferEnglishCategoryCredit() {
		//given
		int beforeTransferCommonCultureCredit = 10;
		int beforeTransferNormalCultureCredit = 0;
		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.commonCultureCredit(beforeTransferCommonCultureCredit)
			.normalCultureCredit(beforeTransferNormalCultureCredit)
			.build();

		//when
		graduationRequirement.transferEnglishCreditCommonToNormal();

		//then
		assertThat(graduationRequirement.getCommonCultureCredit())
			.isEqualTo(beforeTransferCommonCultureCredit - ENGLISH.getTotalCredit());
		assertThat(graduationRequirement.getNormalCultureCredit())
			.isEqualTo(beforeTransferNormalCultureCredit + ENGLISH.getTotalCredit());
	}

	@DisplayName("복수전공 학생 - 주전공, 복수전공, 복수학문기초교양, 일반교양, 자유선택 학점을 조정한다.")
	@Test
	void modifyCreditForDualMajor() {
		//given
		User user = User.builder()
			.primaryMajor("응용소프트웨어전공")
			.dualMajor("경영학전공")
			.entryYear(18)
			.build();

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.totalCredit(80)
			.commonCultureCredit(10)
			.coreCultureCredit(10)
			.primaryMajorCredit(10)
			.dualMajorCredit(10)
			.primaryBasicAcademicalCultureCredit(10)
			.dualBasicAcademicalCultureCredit(10)
			.normalCultureCredit(10)
			.freeElectiveCredit(10)
			.build();

		//when
		graduationRequirement.modifyCreditForDualMajor(user);

		//then
		assertThat(graduationRequirement.getPrimaryMajorCredit()).isEqualTo(ICT.getMajorCredit());
		assertThat(graduationRequirement.getDualMajorCredit()).isEqualTo(BUSINESS_16_24.getMajorCredit());
		assertThat(graduationRequirement.getDualBasicAcademicalCultureCredit()).isEqualTo(
			BUSINESS_16_24.getBasicAcademicalCultureCredit());
		assertThat(graduationRequirement.getNormalCultureCredit()).isEqualTo(0);
		assertThat(graduationRequirement.getFreeElectiveCredit()).isEqualTo(0);
	}

	@DisplayName("25학번 이후 경영대 복수전공은 전공별 학점 기준을 적용한다.")
	@Test
	void modifyCreditForDualMajorAfter2025() {
		GraduationRequirement managementRequirement = GraduationRequirement.builder().build();
		managementRequirement.modifyCreditForDualMajor(User.builder()
			.primaryMajor("경제학전공")
			.dualMajor("경영학전공")
			.entryYear(25)
			.build());

		GraduationRequirement globalRequirement = GraduationRequirement.builder().build();
		globalRequirement.modifyCreditForDualMajor(User.builder()
			.primaryMajor("경제학전공")
			.dualMajor("글로벌비즈니스학전공")
			.entryYear(25)
			.build());

		assertThat(managementRequirement.getDualMajorCredit()).isEqualTo(45);
		assertThat(managementRequirement.getDualBasicAcademicalCultureCredit()).isEqualTo(9);
		assertThat(globalRequirement.getDualMajorCredit()).isEqualTo(36);
		assertThat(globalRequirement.getDualBasicAcademicalCultureCredit()).isEqualTo(6);
	}

	@DisplayName("부전공 학생 - 자유선택 졸업 학점을 부전공 졸업학점으로 조정한다.")
	@Test
	void deleteFreeElectiveCredit() {
		//given
		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.freeElectiveCredit(10)
			.build();

		//when
		graduationRequirement.modifyCreditForSubMajor();

		//then
		assertThat(graduationRequirement.getFreeElectiveCredit()).isZero();
		assertThat(graduationRequirement.getSubMajorCredit()).isEqualTo(21);
	}

}
