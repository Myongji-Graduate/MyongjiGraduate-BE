package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.fixture.BasicAcademicalLectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@DisplayName("기본 단과대에 대해 학문기초교양 결과를 반환한다.")
class DefaultBasicAcademicalManagerTest {

	@DisplayName("인문대의 학문기초교양을 계산한다.")
	@Nested
	class 인문대_학문기초교양 {
		User user = UserFixture.영문학과_18학번();
		StudentInformation studentInformation = user.getStudentInformation();
		Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
		Set<BasicAcademicalCulture> basicAcademicalLectures = BasicAcademicalLectureFixture.인문대_학문기초교양();

		@DisplayName("학문기초교양을 추가해서 들어도 12학점을 초과하지 않으며 통과한다.")
		@Test
		void 영문학과_12학점_이상() {
			//given
			Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMB02127"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMB02119"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMB02120"), 2020, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMB02128"), 2020, Semester.SECOND),
				TakenLecture.of(user, mockLectureMap.get("KMB02122"), 2021, Semester.FIRST)
			)));

			BasicAcademicalManager manager = new DefaultBasicAcademicalManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(studentInformation,
				takenLectures, basicAcademicalLectures, 12);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory().get(0);

			//then
			assertThat(detailGraduationResult)
				.extracting("categoryName", "isCompleted", "totalCredit", "takenCredit")
				.contains("학문기초교양", true, 12, 15);

			assertThat(detailCategoryResult)
				.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
				.contains("학문기초교양", true, 12, 15);

			assertThat(detailCategoryResult.getTakenLectures()).hasSize(5);
			assertThat(detailCategoryResult.getHaveToLectures()).isEmpty();
		}

		@DisplayName("학문기초교양을 다 듣지 못한 경우 통과하지 못한다.")
		@Test
		void 영문학과_12학점_미만() {
			//given
			Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMB02127"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMB02119"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMB02120"), 2020, Semester.FIRST)
			));

			BasicAcademicalManager manager = new DefaultBasicAcademicalManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(studentInformation,
				takenLectures, basicAcademicalLectures, 12);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory().get(0);

			//then
			assertThat(detailGraduationResult)
				.extracting("categoryName", "isCompleted", "totalCredit", "takenCredit")
				.contains("학문기초교양", false, 12, 9);

			assertThat(detailCategoryResult)
				.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
				.contains("학문기초교양", false, 12, 9);

			assertThat(detailCategoryResult.getTakenLectures()).hasSize(3);
			assertThat(detailCategoryResult.getHaveToLectures()).hasSize(10);
		}
	}
}
