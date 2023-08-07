package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicculture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
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

@DisplayName("경영대의 학문기초교양 결과를 반환한다.")
class BusinessBasicAcademicalManagerTest {

	@DisplayName("경영학과의 학문기초교양 결과를 계산한다.")
	@Nested
	class 경영학과_학문기초교양 {

		Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
		Set<BasicAcademicalCulture> basicAcademicalLectures = BasicAcademicalLectureFixture.경영대_학문기초교양();

		@DisplayName("경영대 19학번이 필요 학문 기초교양을 다 들었을 경우 통과한다.")
		@Test
		void 경영학과_19학번() {
			//given
			User user = UserFixture.경영학과_19학번();
			StudentInformation studentInformation = user.getStudentInformation();

			Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMD02114"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMD02107"), 2019, Semester.FIRST)
			)));

			BasicAcademicalManager manager = new BusinessBasicAcademicalManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(studentInformation,
				takenLectures, basicAcademicalLectures, 6);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory().get(0);

			//then
			assertThat(detailGraduationResult)
				.extracting("categoryName", "isCompleted", "totalCredit", "takenCredit")
				.contains("BASIC_ACADEMICAL_CULTURE", true, 6, 6);

			assertThat(detailCategoryResult)
				.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
				.contains("BASIC_ACADEMICAL_CULTURE", true, 6, 6);

			assertThat(detailCategoryResult.getTakenMandatoryLectures()).hasSize(2);
			assertThat(detailCategoryResult.getHaveToMandatoryLectures()).isEmpty();
		}

		@DisplayName("경영대 22학번이 18학번의 필수 학문 기초교양을 들었을 경우 통과하지 다한다.")
		@Test
		void 경영학과_22학번() {

			//given
			User user = UserFixture.경영학과_22학번();
			StudentInformation studentInformation = user.getStudentInformation();

			Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMD02114"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMD02107"), 2019, Semester.FIRST)
			)));
			BasicAcademicalManager manager = new BusinessBasicAcademicalManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(studentInformation,
				takenLectures, basicAcademicalLectures, 6);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory().get(0);

			//then
			assertThat(detailGraduationResult)
				.extracting("categoryName", "isCompleted", "totalCredit", "takenCredit")
				.contains("BASIC_ACADEMICAL_CULTURE", false, 3, 6);

			assertThat(detailCategoryResult)
				.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
				.contains("BASIC_ACADEMICAL_CULTURE", false, 3, 6);

			assertThat(detailCategoryResult.getTakenMandatoryLectures()).hasSize(1);
			assertThat(detailCategoryResult.getHaveToMandatoryLectures()).hasSize(1);
		}

	}

}
