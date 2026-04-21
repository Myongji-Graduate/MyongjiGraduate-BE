package com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserInformationResponseTest {

	@DisplayName("졸업진단 응답의 사용자 정보는 계산된 졸업 결과를 반영한다.")
	@Test
	void ofBuildsResponseFromGraduationResult() {
		User user = User.builder()
			.authId("anonymous")
			.name("방현우")
			.studentNumber("60190872")
			.primaryMajor("국제통상학과")
			.dualMajor("경영정보학과")
			.totalCredit(0)
			.takenCredit(0)
			.graduated(false)
			.build();

		GraduationResult graduationResult = GraduationResult.builder()
			.totalCredit(128)
			.takenCredit(133)
			.graduated(true)
			.build();

		UserInformationResponse response = UserInformationResponse.of(user, graduationResult);

		assertThat(response.getStudentNumber()).isEqualTo("60190872");
		assertThat(response.getStudentName()).isEqualTo("방현우");
		assertThat(response.getTotalCredit()).isEqualTo(128);
		assertThat(response.getTakenCredit()).isEqualTo(133);
		assertThat(response.isGraduated()).isTrue();
		assertThat(response.getCompleteDivision()).hasSize(2);
	}
}
