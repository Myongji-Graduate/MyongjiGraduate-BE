package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class GraduationResultTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("전체 졸업 결과는 남은 수강 과목으로 일반교양, 자유선택 졸업 결과를 처리한다.")
	@Test
	void handleLeftTakenLectures() {
		//given
		User user = UserFixture.데이터테크놀로지학과_19학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA02110"), 2020, Semester.FIRST), //철학과인간
			TakenLecture.of(user, mockLectureMap.get("KMA02111"), 2020, Semester.FIRST), //한국근현대사의이해
			TakenLecture.of(user, mockLectureMap.get("KMA02112"), 2020, Semester.FIRST), //역사와문명
			TakenLecture.of(user, mockLectureMap.get("KMA02140"), 2020, Semester.FIRST), //4차산업혁명을위한비판적사고
			TakenLecture.of(user, mockLectureMap.get("KMA02158"), 2020, Semester.FIRST), //디지털콘텐츠로만나는한국의문화유산
			TakenLecture.of(user, mockLectureMap.get("KMA02113"), 2020, Semester.FIRST), //세계화와사회변화
			TakenLecture.of(user, mockLectureMap.get("KMA02114"), 2020, Semester.FIRST), //민주주의와현대사회
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원론
			TakenLecture.of(user, mockLectureMap.get("HBX01143"), 2021, Semester.FIRST) //운영관리
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		int takenNormalCultureCredit = 21;
		int takenFreeElectiveCredit = 6;

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.normalCultureCredit(15)
			.freeElectiveCredit(12).build();
		GraduationResult graduationResult = GraduationResult.builder()
			.detailGraduationResults(List.of())
			.build();

		//when
		graduationResult.handleLeftTakenLectures(takenLectureInventory, graduationRequirement);

		//then
		assertThat(graduationResult.getNormalCultureGraduationResult())
			.extracting("categoryName", "totalCredit", "takenCredit")
			.contains(NORMAL_CULTURE.getName(), graduationRequirement.getNormalCultureCredit(),
				takenNormalCultureCredit - (takenNormalCultureCredit - graduationRequirement.getNormalCultureCredit()));
		assertThat(graduationResult.getFreeElectiveGraduationResult())
			.extracting("categoryName", "totalCredit", "takenCredit")
			.contains(FREE_ELECTIVE.getName(), graduationRequirement.getFreeElectiveCredit(),
				takenNormalCultureCredit - graduationRequirement.getNormalCultureCredit() + takenFreeElectiveCredit);
	}

	@DisplayName("채플 졸업 결과, 모든 세부 졸업 결과, 일반교양 졸업 결과, 자유선택 졸업 결과가 이수 완료일 시 전체 졸업 결과가 이수 완료이다.")
	@Test
	void checkCompletedGraduated() {
		//given
		int detailCategoryTotalCredit = 10;
		int detailCategoryTakenCredit = 10;
		int normalCultureTotalCredit = 12;
		int normalCultureTakenCredit = 12;
		int freeElectiveTotalCredit = 7;
		int freeElectiveTakenCredit = 7;

		ChapelResult chapelResult = ChapelResult.builder()
			.isCompleted(true).build();
		List<DetailGraduationResult> detailGraduationResults = List.of(DetailGraduationResult.builder()
			.isCompleted(true)
			.categoryName(COMMON_CULTURE.getName())
			.totalCredit(detailCategoryTotalCredit)
			.takenCredit(detailCategoryTakenCredit).build());
		NormalCultureGraduationResult normalCultureGraduationResult = NormalCultureGraduationResult.builder()
			.isCompleted(true)
			.totalCredit(normalCultureTotalCredit)
			.takenCredit(normalCultureTakenCredit).build();
		FreeElectiveGraduationResult freeElectiveGraduationResult = FreeElectiveGraduationResult.builder()
			.isCompleted(true)
			.totalCredit(freeElectiveTotalCredit)
			.takenCredit(freeElectiveTakenCredit).build();

		GraduationResult graduationResult = GraduationResult.builder()
			.chapelResult(chapelResult)
			.detailGraduationResults(detailGraduationResults)
			.normalCultureGraduationResult(normalCultureGraduationResult)
			.freeElectiveGraduationResult(freeElectiveGraduationResult).build();

		//when
		graduationResult.checkGraduated();

		//then
		assertThat(graduationResult.isGraduated()).isTrue();
		assertThat(graduationResult.getTotalCredit()).isEqualTo(
			detailCategoryTotalCredit + normalCultureTotalCredit + freeElectiveTotalCredit);
		assertThat(graduationResult.getTotalCredit()).isEqualTo(
			detailCategoryTakenCredit + normalCultureTakenCredit + freeElectiveTakenCredit);
	}

	@DisplayName("채플 졸업 결과, 모든 세부 졸업 결과, 일반교양 졸업 결과, 자유선택 졸업 결과 중 하나라도 미이수일 시 전체 졸업 결과가 미이수이다.")
	@ParameterizedTest
	@MethodSource("graduationResultFields")
	void checkUnCompletedGraduated(ChapelResult chapelResult, List<DetailGraduationResult> detailGraduationResults,
		NormalCultureGraduationResult normalCultureGraduationResult,
		FreeElectiveGraduationResult freeElectiveGraduationResult) {
		//given
		GraduationResult graduationResult = GraduationResult.builder()
			.chapelResult(chapelResult)
			.detailGraduationResults(detailGraduationResults)
			.normalCultureGraduationResult(normalCultureGraduationResult)
			.freeElectiveGraduationResult(freeElectiveGraduationResult).build();

		//when
		graduationResult.checkGraduated();

		//then
		assertThat(graduationResult.isGraduated()).isFalse();
	}

	static Stream<Arguments> graduationResultFields() {
		return Stream.of(
			Arguments.arguments(
				ChapelResult.builder().isCompleted(true).build(),
				List.of(DetailGraduationResult.builder()
					.isCompleted(true)
					.categoryName(COMMON_CULTURE.getName()).build()),
				NormalCultureGraduationResult.builder().isCompleted(true).build(),
				FreeElectiveGraduationResult.builder().isCompleted(false).build()
			),
			Arguments.arguments(
				ChapelResult.builder().isCompleted(true).build(),
				List.of(DetailGraduationResult.builder()
					.isCompleted(true)
					.categoryName(COMMON_CULTURE.getName()).build()),
				NormalCultureGraduationResult.builder().isCompleted(false).build(),
				FreeElectiveGraduationResult.builder().isCompleted(true).build()
			),
			Arguments.arguments(
				ChapelResult.builder().isCompleted(true).build(),
				List.of(DetailGraduationResult.builder()
					.isCompleted(false)
					.categoryName(COMMON_CULTURE.getName()).build()),
				NormalCultureGraduationResult.builder().isCompleted(true).build(),
				FreeElectiveGraduationResult.builder().isCompleted(true).build()
			),
			Arguments.arguments(
				ChapelResult.builder().isCompleted(false).build(),
				List.of(DetailGraduationResult.builder()
					.isCompleted(true)
					.categoryName(COMMON_CULTURE.getName()).build()),
				NormalCultureGraduationResult.builder().isCompleted(true).build(),
				FreeElectiveGraduationResult.builder().isCompleted(true).build()
			)
		);
	}

}
