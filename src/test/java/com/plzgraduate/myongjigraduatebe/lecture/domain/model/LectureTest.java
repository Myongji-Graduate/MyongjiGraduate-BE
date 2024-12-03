package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LectureTest {

	@DisplayName("과목코드가 'KM'으로 시작하는 과목은 교양과목이다.")
	@Test
	void checkIsCulture() {
		//given
		String lectureCode = "KMA02100";
		Lecture lecture = Lecture.from(lectureCode);

		//when
		boolean isCulture = lecture.isCulture();

		//then
		assertThat(isCulture).isTrue();
	}

	@DisplayName("과목코드가 'KM'으로 시작하지 않는 과목은 교양과목이 아니다.")
	@Test
	void checkNotIsCulture() {
		//given
		String lectureCode = "HEB01102";
		Lecture lecture = Lecture.from(lectureCode);

		//when
		boolean isCulture = lecture.isCulture();

		//then
		assertThat(isCulture).isFalse();
	}

}
