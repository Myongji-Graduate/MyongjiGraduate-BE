package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.Test;

class HonorsCollegeMajorPolicyTest {

	@Test
	void mapsAnHonorsStudentToTheExplorationSeminarForTheirEnteredMajor() {
		User user = User.builder()
			.entryYear(25)
			.honorsCollege(true)
			.primaryMajor("경영학전공")
			.build();

		assertThat(HonorsCollegeMajorPolicy.findMandatoryExplorationSeminar(user, MajorType.PRIMARY))
			.contains("HBY01106");
	}

	@Test
	void usesTheSelectedHonorsTargetMajorInsteadOfTheMajorPrintedOnTheTranscript() {
		User user = User.builder()
			.entryYear(25)
			.honorsCollege(true)
			.primaryMajor("데이터사이언스전공")
			.honorsTargetMajor("경영학전공")
			.build();

		assertThat(user.getPrimaryMajor()).isEqualTo("경영학전공");
		assertThat(HonorsCollegeMajorPolicy.findMandatoryExplorationSeminar(user, MajorType.PRIMARY))
			.contains("HBY01106");
	}

	@Test
	void excludesGlobalBusinessAndNonHonorsStudents() {
		User globalBusinessUser = User.builder()
			.entryYear(25)
			.honorsCollege(true)
			.primaryMajor("글로벌비즈니스학전공")
			.build();
		User normalUser = User.builder()
			.entryYear(25)
			.primaryMajor("경영학전공")
			.build();

		assertThat(HonorsCollegeMajorPolicy.findMandatoryExplorationSeminar(globalBusinessUser, MajorType.PRIMARY))
			.isEmpty();
		assertThat(HonorsCollegeMajorPolicy.findMandatoryExplorationSeminar(normalUser, MajorType.PRIMARY))
			.isEmpty();
	}
}
