package com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture;

import static com.plzgraduate.myongjigraduatebe.fixture.CoreCultureFixture.*;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.plzgraduate.myongjigraduatebe.fixture.CoreCultureCategoryFixture;
import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@DisplayName("각 핵심교양 세부 카테고리 별 카테고리 이름, 총 학점, 이수 여부를 포함한 카테고리 졸업 결과를 생성한다.")
class CoreCultureDetailCategoryManagerTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
	CoreCultureDetailCategoryManager manager = new CoreCultureDetailCategoryManager();

	@DisplayName("각 카테고리의 해당하는 과목의 이수 학점을 만족한 경우 이수 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CoreCultureCategoryFixture.class)
	void generateCompletedCoreCultureDetailCategoryResult(CoreCultureCategory coreCultureCategory,
		Set<CoreCulture> graduationLectures) {
		//given
		User user = UserFixture.경영학과_19학번();
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
		String coreCultureCategoryName = coreCultureCategory.getName();
		int categoryTotalCredit = coreCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user.getStudentInformation(), takenLectures,
			graduationLectures,
			coreCultureCategory);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(coreCultureCategoryName, true, categoryTotalCredit);
	}

	@DisplayName("각 카테고리의 해당하는 과목의 이수 학점을 만족하지 못한 경우 이수 미 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CoreCultureCategoryFixture.class)
	void generateUnCompletedCoreCultureDetailCategoryResult(CoreCultureCategory coreCultureCategory,
		Set<CoreCulture> graduationLectures) {
		//given
		User user = UserFixture.경영학과_19학번();
		Set<TakenLecture> takenLectures = new HashSet<>();
		String coreCultureCategoryName = coreCultureCategory.getName();
		int categoryTotalCredit = coreCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user.getStudentInformation(), takenLectures,
			graduationLectures, coreCultureCategory);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(coreCultureCategoryName, false, categoryTotalCredit);
	}

	@DisplayName("ICT학부 전공 학생은 핵심교양 세부 카테고리 '과학과기술' 중 SW프로그래밍입문을 수강했을 경우 카테고리 수강 학점이 아닌 자유학점으로 인정된다.")
	@ParameterizedTest
	@MethodSource("ictUsers")
	void generateUnCompletedScienceTechnologyDetailCategoryResultWithICT(User user) {
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA02135"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02136"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02138"), 2019, Semester.FIRST)
		)));
		Set<CoreCulture> graduationLectures = 핵심교양_과학과기술();
		CoreCultureCategory coreCultureCategory = SCIENCE_TECHNOLOGY;
		int categoryTotalCredit = coreCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user.getStudentInformation(), takenLectures,
			graduationLectures, coreCultureCategory);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits", "normalLeftCredit",
				"freeElectiveLeftCredit")
			.contains(coreCultureCategory.getName(), true, categoryTotalCredit, 3, 3);
	}
	static Stream<Arguments> ictUsers() {
		return Stream.of(
			Arguments.arguments(UserFixture.응용소프트웨어학과_19학번()),
			Arguments.arguments(UserFixture.데이터테크놀로지학과_19학번()),
			Arguments.arguments(UserFixture.디지털콘텐츠디자인학과_19학번())
		);
	}
}
