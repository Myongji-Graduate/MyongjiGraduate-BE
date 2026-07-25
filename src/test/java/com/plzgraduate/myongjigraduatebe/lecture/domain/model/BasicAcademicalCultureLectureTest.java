package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BasicAcademicalCultureLectureTest {

	@DisplayName("수강 시작·종료 학기를 포함한 기간 안에서만 학문기초로 인정한다.")
	@ParameterizedTest
	@CsvSource({
		"2022, SECOND, false",
		"2023, FIRST, true",
		"2025, SECOND, true",
		"2026, FIRST, false"
	})
	void recognizesTakenPeriod(int year, Semester semester, boolean expected) {
		Lecture lecture = Lecture.from("KME02101", "미적분학1", 3);
		BasicAcademicalCultureLecture policy = BasicAcademicalCultureLecture.builder()
			.lecture(lecture)
			.college("인공지능·소프트웨어융합대학")
			.startTakenYear(2023)
			.startTakenSemester(Semester.FIRST)
			.endTakenYear(2025)
			.endTakenSemester(Semester.SECOND)
			.build();

		assertThat(policy.recognizes(TakenLecture.of(null, lecture, year, semester)))
			.isEqualTo(expected);
	}

	@DisplayName("동일과목 코드는 정책의 대표 교과코드와 달라도 인정한다.")
	@Test
	void recognizesEquivalentLecture() {
		Lecture policyLecture = Lecture.of("OLD", "구과목", 3, 0, "SAME");
		Lecture takenLecture = Lecture.of("NEW", "신과목", 3, 0, "SAME");
		BasicAcademicalCultureLecture policy =
			BasicAcademicalCultureLecture.of(policyLecture, "인문대");

		assertThat(policy.recognizes(
			TakenLecture.of(null, takenLecture, 2026, Semester.FIRST)))
			.isTrue();
	}
}
