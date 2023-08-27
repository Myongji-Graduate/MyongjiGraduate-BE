package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class NormalCultureGraduationResultTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("처리되지 않은 일반교양 수강과목과 다른 카테고리(공통, 핵심, 학문기초교양, 전공)의 남은 일반교양 학점으로 일반교양 졸업 결과를 생성한다.")
	@Test
	void createNormalCultureGraduationResult() {
		//given
		User user = UserFixture.경영학과_19학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST)
			)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);

		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
			.categoryName(COMMON_CULTURE.getName())
			.detailCategory(List.of(
				DetailCategoryResult.builder()
					.detailCategoryName(CHRISTIAN_A.getName())
					.normalLeftCredit(3).build(),
				DetailCategoryResult.builder()
					.detailCategoryName(CAREER.getName())
					.normalLeftCredit(3).build()
			)).build();

		int remainTakenNormalCultureCredit = takenLectureInventory.getCultureLectures().stream()
			.mapToInt(takenLecture -> takenLecture.getLecture().getCredit())
			.sum();
		int remainCreditByDetailGraduationResult = detailGraduationResult.getNormalLeftCredit();

		//when
		NormalCultureGraduationResult normalCultureGraduationResult = NormalCultureGraduationResult.create(15,
			takenLectureInventory,
			List.of(detailGraduationResult));

		//then
		assertThat(normalCultureGraduationResult)
			.extracting("categoryName", "takenCredit")
			.contains(NORMAL_CULTURE.getName(), remainTakenNormalCultureCredit + remainCreditByDetailGraduationResult);
	}

}
