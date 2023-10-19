package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@DisplayName("사회과학대의 학문기초교양 결과를 반환한다.")
class SocialScienceBasicAcademicManagerTest {

	@DisplayName("2023년도 1학기 이후 교과목을 포함한다.")
	@Nested
	class 이십삼년도_이후_교과목_포함 {
		User user = UserFixture.행정학과_21학번();
		Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
		Set<BasicAcademicalCultureLecture> basicAcademicalLectures = BasicAcademicalLectureFixture.사회과학대_학문기초교양();

		@DisplayName("2023년도 이후에 들었을 경우 통과한다.")
		@Test
		void 이삽삼년도_이후_수강() {
			//given
			Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMD02109"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMB02103"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMD02108"), 2023, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMD02186"), 2023, Semester.SECOND)
			)));
			TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
			BasicAcademicalManager manager = new DefaultBasicAcademicalManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
				takenLectureInventory, basicAcademicalLectures, 12);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory().get(0);

			//then
			assertThat(detailGraduationResult)
				.extracting("categoryName", "isCompleted", "totalCredit", "takenCredit")
				.contains("학문기초교양", true, 12, 12);

			assertThat(detailCategoryResult)
				.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
				.contains("학문기초교양", true, 12, 12);

			assertThat(detailCategoryResult.getTakenLectures()).hasSize(4);
			assertThat(detailCategoryResult.getHaveToLectures()).isEmpty();
		}

		@DisplayName("2023년도 이전에 들었을 경우 통과하지 못한다.")
		@Test
		void 이십산년도_이전_수강() {

			//given
			Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMD02109"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMB02103"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMD02108"), 2020, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMD02186"), 2020, Semester.SECOND)
			)));
			TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
			BasicAcademicalManager manager = new SocialScienceBasicAcademicManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
				takenLectureInventory, basicAcademicalLectures, 12);
			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory().get(0);

			//then
			assertThat(detailGraduationResult)
				.extracting("categoryName", "isCompleted", "totalCredit", "takenCredit")
				.contains("학문기초교양", false, 12, 6.0);

			assertThat(detailCategoryResult)
				.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
				.contains("학문기초교양", false, 12, 6);

			assertThat(detailCategoryResult.getTakenLectures()).hasSize(2);
			assertThat(detailCategoryResult.getHaveToLectures()).hasSize(17);
		}

	}

}
