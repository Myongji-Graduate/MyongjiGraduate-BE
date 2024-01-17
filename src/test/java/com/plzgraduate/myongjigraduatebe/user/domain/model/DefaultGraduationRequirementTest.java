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

class DefaultGraduationRequirementTest {

	@DisplayName("학생의 소속 단과대와 입학년도로 기본졸업요건을 결정한다.")
	@ParameterizedTest()
	@ValueSource(ints = {16, 18})
	void determineGraduationRequirement(int entryYear) {
		//given
		College ict = College.ICT;

		//when
		DefaultGraduationRequirement graduationRequirement = DefaultGraduationRequirement.determineGraduationRequirement(
			ict,
			entryYear);

		//then
		assertThat(graduationRequirement.getCollageName()).isEqualTo(ict.getName());
		assertThat(graduationRequirement.getStartEntryYear()).isLessThanOrEqualTo(entryYear);
		assertThat(graduationRequirement.getEndEntryYear()).isGreaterThan(entryYear);
	}

	@DisplayName("학생의 입학년도가 16년도 미만일 경우 에러를 발생시킨다.")
	@ParameterizedTest()
	@ValueSource(ints = {14, 15})
	void determineGraduationRequirementWithIllegalUser(int entryYear) {
		//given
		College ict = College.ICT;

		//when //then
		assertThatThrownBy(() -> DefaultGraduationRequirement.determineGraduationRequirement(ict, entryYear))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("일치하는 졸업 요건이 존재하지 않습니다.");
	}

	@DisplayName("영어 면제 학생의 졸업요건을 매핑한다.")
	@Test()
	void convertToProfitGraduationRequirementWithFreeEnglishUser() {
		//given
		User freeEnglishUser = UserFixture.경영학과_19학번_영어_면제();
		College ict = College.ICT;
		DefaultGraduationRequirement defaultGraduationRequirement = DefaultGraduationRequirement.determineGraduationRequirement(
			ict, freeEnglishUser.getEntryYear());

		//when
		GraduationRequirement graduationRequirement = defaultGraduationRequirement.convertToProfitGraduationRequirement(
			freeEnglishUser);

		// then
		assertThat(graduationRequirement.getCommonCultureCredit()).isEqualTo(
			defaultGraduationRequirement.getCommonCultureCredit() - ENGLISH.getTotalCredit());
		assertThat(graduationRequirement.getNormalCultureCredit()).isEqualTo(
			defaultGraduationRequirement.getNormalLectureCredit() + ENGLISH.getTotalCredit());
	}
}
