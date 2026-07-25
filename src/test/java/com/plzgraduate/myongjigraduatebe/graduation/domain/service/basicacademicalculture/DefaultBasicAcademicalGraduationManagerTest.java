package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("기본 단과대에 대해 학문기초교양 결과를 반환한다.")
class DefaultBasicAcademicalGraduationManagerTest {

	@Test
	@DisplayName("과거 수강은 인정하지만 인정 기간이 끝난 과목은 미이수 목록에서 제외한다.")
	void expiredPolicyRecognizesPastTakenLectureButIsNotRecommended() {
		User user = UserFixture.영문학과_18학번();
		int currentYear = LocalDate.now(ZoneId.of("Asia/Seoul")).getYear();
		Lecture takenExpired = Lecture.of("EXPIRED-TAKEN", "과거이수과목", 3, 0, "EXPIRED-TAKEN");
		Lecture untakenExpired = Lecture.of("EXPIRED-UNTKEN", "과거미이수과목", 3, 0, "EXPIRED-UNTKEN");
		Lecture current = Lecture.of("CURRENT", "현재인정과목", 3, 0, "CURRENT");
		Set<BasicAcademicalCultureLecture> policies = Set.of(
			policyEndingAt(takenExpired, currentYear - 1),
			policyEndingAt(untakenExpired, currentYear - 1),
			BasicAcademicalCultureLecture.builder().lecture(current).college("인문대학").build()
		);
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
			TakenLecture.of(user, takenExpired, currentYear - 1, Semester.SECOND)
		));

		DetailCategoryResult result = new DefaultBasicAcademicalGraduationManager()
			.createDetailGraduationResult(user, inventory, policies, 6)
			.getDetailCategory()
			.get(0);

		assertThat(result.getTakenLectures()).extracting(Lecture::getId)
			.containsExactly("EXPIRED-TAKEN");
		assertThat(result.getHaveToLectures()).extracting(Lecture::getId)
			.containsExactly("CURRENT");
	}

	private BasicAcademicalCultureLecture policyEndingAt(Lecture lecture, int year) {
		return BasicAcademicalCultureLecture.builder()
			.lecture(lecture)
			.college("인문대학")
			.endTakenYear(year)
			.endTakenSemester(Semester.SECOND)
			.build();
	}

	@DisplayName("인문대의 학문기초교양을 계산한다.")
	@Nested
	class 인문대_학문기초교양 {

		User user = UserFixture.영문학과_18학번();
		User user2 = UserFixture.영문학과_18학번_교환_학문기초3학점();
		Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
		Set<BasicAcademicalCultureLecture> basicAcademicalLectures = BasicAcademicalLectureFixture.인문대_학문기초교양();

		@DisplayName("학문기초교양을 추가해서 들어도 12학점을 초과하지 않으면 통과한다.")
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
			TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
			BasicAcademicalGraduationManager manager = new DefaultBasicAcademicalGraduationManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(
				user,
				takenLectureInventory, basicAcademicalLectures, 12);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory()
				.get(0);

			//then
			assertThat(detailGraduationResult)
				.extracting("isCompleted", "totalCredit", "takenCredit")
				.contains(true, 12, 12);

			assertThat(detailCategoryResult)
				.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
				.contains("학문기초교양", true, 12, 12);

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
			TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
			BasicAcademicalGraduationManager manager = new DefaultBasicAcademicalGraduationManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(
				user,
				takenLectureInventory, basicAcademicalLectures, 12);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory()
				.get(0);

			//then
			assertThat(detailGraduationResult)
				.extracting("isCompleted", "totalCredit", "takenCredit")
				.contains(false, 12, 9.0);

			assertThat(detailCategoryResult)
				.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
				.contains("학문기초교양", false, 12, 9);

			assertThat(detailCategoryResult.getTakenLectures()).hasSize(3);
			assertThat(detailCategoryResult.getHaveToLectures()).hasSize(10);
		}


		@DisplayName("교환학생 학점이 추가된 경우 학문기초교양을 통과한다.")
		@Test
		void 영문학과_교환학생_학점_추가() {
			//given
			Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
					TakenLecture.of(user2, mockLectureMap.get("KMB02127"), 2019, Semester.FIRST),
					TakenLecture.of(user2, mockLectureMap.get("KMB02119"), 2019, Semester.FIRST),
					TakenLecture.of(user2, mockLectureMap.get("KMB02120"), 2020, Semester.FIRST)
			));
			TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
			BasicAcademicalGraduationManager manager = new DefaultBasicAcademicalGraduationManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(
					user2,
					takenLectureInventory, basicAcademicalLectures, 12);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory()
					.get(0);

			//then
			assertThat(detailGraduationResult)
					.extracting("isCompleted", "totalCredit", "takenCredit")
					.contains(true, 12, 12.0); // 교환학생 학점 포함하여 12학점 달성

			assertThat(detailCategoryResult)
					.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
					.contains("학문기초교양", true, 12, 12);

			assertThat(detailCategoryResult.getTakenLectures()).hasSize(3);
			assertThat(detailCategoryResult.getHaveToLectures()).hasSize(10);

		}

		@DisplayName("교환학생 학점이 추가되었지만 부족한 학점으로 인해 통과하지 못한다.")
		@Test
		void 영문학과_교환학생_학점_부족() {
			//given

			Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
					TakenLecture.of(user2, mockLectureMap.get("KMB02127"), 2019, Semester.FIRST),
					TakenLecture.of(user2, mockLectureMap.get("KMB02119"), 2019, Semester.FIRST)
			));
			TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
			BasicAcademicalGraduationManager manager = new DefaultBasicAcademicalGraduationManager();

			//when
			DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(
					user2,
					takenLectureInventory, basicAcademicalLectures, 12);

			DetailCategoryResult detailCategoryResult = detailGraduationResult.getDetailCategory()
					.get(0);

			//then
			assertThat(detailGraduationResult)
					.extracting("isCompleted", "totalCredit", "takenCredit")
					.contains(false, 12, 9.0); // 교환학생 학점 포함하여도 9학점으로 부족

			assertThat(detailCategoryResult)
					.extracting("detailCategoryName", "isCompleted", "totalCredits", "takenCredits")
					.contains("학문기초교양", false, 12, 9);

			assertThat(detailCategoryResult.getTakenLectures()).hasSize(2); // 실제 들은 과목 2개
			assertThat(detailCategoryResult.getHaveToLectures()).hasSize(11); // 부족한 과목
		}
	}
}
