package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.NORMAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CAREER;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CHRISTIAN_A;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NormalCultureGraduationResultTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("처리되지 않은 일반교양 수강과목과 다른 카테고리(공통, 핵심, 학문기초교양, 전공)의 남은 일반교양 학점으로 일반교양 졸업 결과를 생성한다.")
	@Test
	void createNormalCultureGraduationResult() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
			.graduationCategory(COMMON_CULTURE)
			.detailCategory(List.of(
				DetailCategoryResult.builder()
					.detailCategoryName(CHRISTIAN_A.getName())
					.normalLeftCredit(3)
					.build(),
				DetailCategoryResult.builder()
					.detailCategoryName(CAREER.getName())
					.normalLeftCredit(3)
					.build()
			))
			.build();

		int remainTakenNormalCultureCredit = takenLectureInventory.getCultureLectures()
			.stream()
			.mapToInt(takenLecture -> takenLecture.getLecture()
				.getCredit())
			.sum();
		int remainCreditByDetailGraduationResult = detailGraduationResult.getNormalLeftCredit();

		//when
		NormalCultureGraduationResult normalCultureGraduationResult = NormalCultureGraduationResult.create(
			15,
			0,
			takenLectureInventory,
			List.of(detailGraduationResult));

		//then
		assertThat(normalCultureGraduationResult)
			.extracting("categoryName", "takenCredit")
			.contains(NORMAL_CULTURE.getName(),
				remainTakenNormalCultureCredit + remainCreditByDetailGraduationResult);
	}
	@DisplayName("봉사학점은 일반교양 학점에 포함되지 않는다.")
	@Test
	void volunteerCreditIsNotIncludedInNormalCulture() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02198"), 2021, Semester.FIRST) // 봉사학점
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		//when
		NormalCultureGraduationResult normalCultureGraduationResult = NormalCultureGraduationResult.create(
				9,
				0,
				takenLectureInventory,
				List.of());

		//then
		assertThat(normalCultureGraduationResult)
				.extracting("takenCredit")
				.isEqualTo(4); // 봉사학점(1학점)은 제외되고, 나머지 일반교양 학점만 포함
	}
	@DisplayName("일반교양 학점에 교환학생 인정 학점을 포함하여 결과를 계산한다.")
	@Test
	void includeExchangeCreditsInNormalCulture() {
		// given
		User user = UserFixture.경영학과_19학번_ENG34_교환학생_일반학점_3();

		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
				.graduationCategory(NORMAL_CULTURE)
				.detailCategory(List.of(
						DetailCategoryResult.builder()
								.detailCategoryName(CHRISTIAN_A.getName())
								.normalLeftCredit(3)
								.build(),
						DetailCategoryResult.builder()
								.detailCategoryName(CAREER.getName())
								.normalLeftCredit(3)
								.build()
				))
				.build();

		int takenNormalCultureCredit = takenLectureInventory.getCultureLectures()
				.stream()
				.mapToInt(takenLecture -> takenLecture.getLecture().getCredit())
				.sum();
		int remainCredit = detailGraduationResult.getNormalLeftCredit();
		int exchangeCredit = user.getExchangeCredit().getNormalCulture();

		int acknowledgedCredit = user.getExchangeCredit().getNormalCulture();
		// when
		NormalCultureGraduationResult normalCultureGraduationResult = NormalCultureGraduationResult.create(
				15,
				acknowledgedCredit,
				takenLectureInventory,
				List.of(detailGraduationResult));


		// then
		assertThat(normalCultureGraduationResult)
				.extracting("categoryName", "takenCredit")
				.contains(NORMAL_CULTURE.getName(),
						takenNormalCultureCredit + remainCredit + exchangeCredit);
	}
}
