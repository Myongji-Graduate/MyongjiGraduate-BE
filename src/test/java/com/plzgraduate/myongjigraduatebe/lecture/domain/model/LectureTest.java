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

	@DisplayName("동일과목 대표 코드가 있으면 대표 코드를 인정 코드로 사용한다.")
	@Test
	void getRecognitionCodeWithDuplicateCode() {
		Lecture lecture = Lecture.of("KMC02234", "4차산업혁명시대의예술", 3, 0, "KMA02155");

		assertThat(lecture.getRecognitionCode()).isEqualTo("KMA02155");
	}

	@DisplayName("동일과목 대표 코드가 없으면 과목 코드를 인정 코드로 사용한다.")
	@Test
	void getRecognitionCodeWithoutDuplicateCode() {
		Lecture lecture = Lecture.of("KMA02155", "4차산업혁명시대의예술", 3, 0, null);

		assertThat(lecture.getRecognitionCode()).isEqualTo("KMA02155");
	}

}
