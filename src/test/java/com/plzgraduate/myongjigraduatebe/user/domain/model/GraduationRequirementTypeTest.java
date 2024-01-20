package com.plzgraduate.myongjigraduatebe.user.domain.model;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;
import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;

class GraduationRequirementTypeTest {

	@DisplayName("학생의 소속 단과대와 입학년도로 기본졸업요건을 결정한다.")
	@ParameterizedTest()
	@ValueSource(ints = {16, 18})
	void determineGraduationRequirement(int entryYear) {
		//given
		College ict = College.ICT;
		User user = User.builder()
			.entryYear(entryYear).build();

		//when
		GraduationRequirementType graduationRequirement = GraduationRequirementType.determineGraduationRequirement(ict,
			user);

		//then
		assertThat(graduationRequirement.getCollageName()).isEqualTo(ict.getName());
		assertThat(graduationRequirement.getStartEntryYear()).isLessThanOrEqualTo(user.getEntryYear());
		assertThat(graduationRequirement.getEndEntryYear()).isGreaterThan(user.getEntryYear());
	}

	@DisplayName("학생의 입학년도가 16년도 미만일 경우 에러를 발생시킨다.")
	@ParameterizedTest()
	@ValueSource(ints = {14, 15})
	void determineGraduationRequirementWithIllegalUser(int entryYear) {
		//given
		College ict = College.ICT;
		User user = User.builder()
			.entryYear(entryYear).build();

		//when //then
		assertThatThrownBy(() -> GraduationRequirementType.determineGraduationRequirement(ict, user))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("일치하는 졸업 요건이 존재하지 않습니다.");
	}

	@DisplayName("영어 면제 학생의 졸업요건을 결정한다.")
	@Test()
	void convertToProfitGraduationRequirementWithFreeEnglishUser() {
		//given
		User freeEnglishUser = UserFixture.경영학과_19학번_영어_면제();
		College ict = College.ICT;
		GraduationRequirementType graduationRequirementType = GraduationRequirementType.determineGraduationRequirement(
			ict, freeEnglishUser);

		//when
		GraduationRequirement graduationRequirement = graduationRequirementType.convertToProfitGraduationRequirement(
			freeEnglishUser);

		// then
		assertThat(graduationRequirement.getCommonCultureCredit()).isEqualTo(
			graduationRequirementType.getCommonCultureCredit() - ENGLISH.getTotalCredit());
		assertThat(graduationRequirement.getNormalCultureCredit()).isEqualTo(
			graduationRequirementType.getNormalLectureCredit() + ENGLISH.getTotalCredit());
	}

	@DisplayName("부전공 학생의 졸업요건을 결정한다.")
	@Test()
	void convertToProfitGraduationRequirementWithSubMajorUser() {
		//given
		User subMajorUser = UserFixture.경영학과_23학번_국제통상학과_부전공();
		College ict = College.BUSINESS;
		GraduationRequirementType graduationRequirementType = GraduationRequirementType.determineGraduationRequirement(
			ict, subMajorUser);

		//when
		GraduationRequirement graduationRequirement = graduationRequirementType.convertToProfitGraduationRequirement(
			subMajorUser);

		// then
		assertThat(graduationRequirement.getFreeElectiveCredit()).isZero();

	}
}
