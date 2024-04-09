package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.FREE_ELECTIVE;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CHRISTIAN_A;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.*;
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

class FreeElectiveGraduationResultTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("처리되지 않은 전공 수강과목과 다른 카테고리(공통, 핵심, 학문기초교양, 전공, 일반교양)의 남은 자유선택 학점으로 자유선택 졸업 결과를 생성한다.")
	@Test
	void createFreeElectiveGraduationResult() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("HBX01104"), 2019, Semester.FIRST), //회계원리
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2019, Semester.FIRST), //인적자원관리
			TakenLecture.of(user, mockLectureMap.get("HBX01106"), 2020, Semester.FIRST), //마케팅원론
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원론
			TakenLecture.of(user, mockLectureMap.get("HBX01143"), 2021, Semester.FIRST) //운영관리
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
			.graduationCategory(FREE_ELECTIVE)
			.detailCategory(List.of(
				DetailCategoryResult.builder()
					.detailCategoryName(CHRISTIAN_A.getName())
					.freeElectiveLeftCredit(3).build(),
				DetailCategoryResult.builder()
					.detailCategoryName(SCIENCE_TECHNOLOGY.getName())
					.freeElectiveLeftCredit(3).build()
			)).build();

		int remainCreditByTakenLectures = takenLectureInventory.getTakenLectures().stream()
			.mapToInt(takenLecture -> takenLecture.getLecture().getCredit())
			.sum();
		int freeElectiveLeftCredit = detailGraduationResult.getFreeElectiveLeftCredit();
		int leftNormalCultureCredit = 5;

		//when
		FreeElectiveGraduationResult freeElectiveGraduationResult = FreeElectiveGraduationResult.create(7,
			takenLectureInventory, List.of(detailGraduationResult),
			leftNormalCultureCredit);

		//then
		assertThat(freeElectiveGraduationResult)
			.extracting("categoryName", "takenCredit")
			.contains(FREE_ELECTIVE.getName(),
				remainCreditByTakenLectures + freeElectiveLeftCredit + leftNormalCultureCredit);
	}

}
