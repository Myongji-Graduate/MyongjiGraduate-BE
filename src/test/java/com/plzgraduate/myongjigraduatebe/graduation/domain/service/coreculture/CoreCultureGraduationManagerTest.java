package com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.fixture.CoreCultureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@DisplayName("각 핵심교양 세부 카테고리 졸업 결과를 포함한 공통교양 전체 졸업 결과를 생성한다.")
class CoreCultureGraduationManagerTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
	GraduationManager<CoreCulture> graduationManager = new CoreCultureGraduationManager();

	@DisplayName("모든 핵심교양 세부 카테고리가 이수 완료일 경우 이수 완료 핵심교양 전체 졸업 결과를 생성한다.")
	@Test
	void generateCompletedDetailGraduationResult() {
    
		//given
		User user = UserFixture.영문학과_16학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA02110"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02111"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02112"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02140"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02158"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02113"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02114"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02131"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02142"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02160"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02128"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02130"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02132"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02155"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02156"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02159"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02119"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02120"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02127"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02135"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02136"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02138"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02139"), 2023, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		Set<CoreCulture> graduationLectures = CoreCultureFixture.getAllCoreCulture();

		//when
		DetailGraduationResult detailGraduationResult = graduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationLectures, 12);

		//then
		assertThat(detailGraduationResult)
			.extracting("categoryName", "isCompleted")
			.contains("핵심교양", true);
	}

	@DisplayName("모든 핵심교양 세부 카테고리가 이수 완료가 아닐 경우 이수 미 완료 핵심교양 전체 졸업 결과를 생성한다.")
	@Test
	void generateUnCompletedDetailGraduationResult() {
    
		//given
		User user = UserFixture.영문학과_16학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA02110"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02111"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02112"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02140"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02158"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02113"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02114"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02131"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02142"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02160"), 2023, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		Set<CoreCulture> graduationLectures = CoreCultureFixture.getAllCoreCulture();

		//when
		DetailGraduationResult detailGraduationResult = graduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationLectures, 12);

		//then
		assertThat(detailGraduationResult)
			.extracting("categoryName", "isCompleted")
			.contains("핵심교양", false);
	}

}
