package com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture;


import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.CommonCultureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@DisplayName("각 공통교양 세부 카테고리 졸업 결과를 포함한 공통교양 전체 졸업 결과를 생성한다.")
class CommonCultureGraduationManagerTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
	GraduationManager<CommonCulture> graduationManager = new CommonGraduationManager();

	@DisplayName("모든 공통교양 세부 카테고리가 이수 완료일 경우 이수 완료 공통교양 전체 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureFixture.class)
	void generateCompletedDetailGraduationResult(User user, Set<CommonCulture> graduationLectures) {
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02106"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02107"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02123"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02124"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02108"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02109"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02125"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02126"), 2023, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		//when
		DetailGraduationResult detailGraduationResult = graduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationLectures, 17);

		//then
		assertThat(detailGraduationResult)
			.extracting("graduationCategory", "isCompleted")
			.contains(COMMON_CULTURE, true);
	}

	@DisplayName("모든 공통교양 세부 카테고리가 이수 완료가 아닐 경우 이수 미 완료 공통교양 전체 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureFixture.class)
	void generateUnCompletedDetailGraduationResult(User user,
		Set<CommonCulture> graduationLectures) {
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		//when
		DetailGraduationResult detailGraduationResult = graduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationLectures, 17);

		//then
		assertThat(detailGraduationResult)
			.extracting("graduationCategory", "isCompleted")
			.contains(COMMON_CULTURE, false);
	}

}
