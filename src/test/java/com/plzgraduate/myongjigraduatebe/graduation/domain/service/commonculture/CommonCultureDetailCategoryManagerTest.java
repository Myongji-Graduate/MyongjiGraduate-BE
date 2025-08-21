package com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture;

import static com.plzgraduate.myongjigraduatebe.fixture.CommonCultureFixture.공통교양_16_17;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CHRISTIAN_A;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.CommonCultureCategoryFixture;
import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@DisplayName("각 공통교양 세부 카테고리 별 카테고리 이름, 총 학점, 이수 여부를 포함한 카테고리 졸업 결과를 생성한다.")
class CommonCultureDetailCategoryManagerTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
	CommonCultureDetailCategoryManager manager = new CommonCultureDetailCategoryManager();

	@DisplayName("영어 레벨 기초: 각 카테고리의 해당하는 과목의 이수 학점을 만족한 경우 이수 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureCategoryFixture.class)
	void generateEngBasicCompletedCommonCultureDetailCategory(
		CommonCultureCategory commonCultureCategory,
		Set<CommonCulture> graduationLectures
	) {
		//given
		User user = UserFixture.데이테크놀로지전공_18학번_Basic_Eng();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMP02126"), 2019, Semester.FIRST),
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
		String commonCultureCategoryName = commonCultureCategory.getName();
		int expectedTotalCredit = (commonCultureCategory == CommonCultureCategory.KOREAN) ? 0 : commonCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			commonCultureCategory
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(commonCultureCategoryName, true, expectedTotalCredit);
	}

	@DisplayName("영어 레벨 12: 각 카테고리의 해당하는 과목의 이수 학점을 만족한 경우 이수 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureCategoryFixture.class)
	void generateEng12CompletedCommonCultureDetailCategory(
		CommonCultureCategory commonCultureCategory,
		Set<CommonCulture> graduationLectures
	) {
		//given
		User user = UserFixture.경영학과_19학번_ENG12();
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
		String commonCultureCategoryName = commonCultureCategory.getName();
		int expectedTotalCredit = (commonCultureCategory == CommonCultureCategory.KOREAN) ? 0 : commonCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			commonCultureCategory
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(commonCultureCategoryName, true, expectedTotalCredit);
	}

	@DisplayName("영어 레벨 34: 각 카테고리의 해당하는 과목의 이수 학점을 만족한 경우 이수 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureCategoryFixture.class)
	void generateEng34CompletedCommonCultureDetailCategory(
		CommonCultureCategory commonCultureCategory,
		Set<CommonCulture> graduationLectures
	) {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
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
		String commonCultureCategoryName = commonCultureCategory.getName();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			commonCultureCategory
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted")
			.contains(commonCultureCategoryName, true);
	}

	@DisplayName("영어 레벨 FREE: 각 카테고리의 해당하는 과목의 이수 학점을 만족한 경우 이수 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureCategoryFixture.class)
	void generateEngFreeCompletedCommonCultureDetailCategory(
		CommonCultureCategory commonCultureCategory,
		Set<CommonCulture> graduationLectures
	) {
		//given
		User user = UserFixture.경영학과_19학번_영어_면제();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		String commonCultureCategoryName = commonCultureCategory.getName();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			commonCultureCategory
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted")
			.contains(commonCultureCategoryName, true);
	}

	@DisplayName(
		"영어 레벨 기초: 각 카테고리의 해당하는 과목의 이수 학점을 만족하지 못한 경우(기초영어 미수강) 이수 미 완료의 카테고리 졸업 결과를 생성한다."
	)
	@Test
	void generateEngBasicUnCompletedCommonCultureDetailCategory() {
		//given
		User user = UserFixture.데이테크놀로지전공_18학번_Basic_Eng();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02106"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02107"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02108"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02109"), 2023, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		CommonCultureCategory commonCultureCategory = ENGLISH;
		Set<CommonCulture> graduationLectures = new HashSet<>(Set.of(
			CommonCulture.of(mockLectureMap.get("KMP02126"), commonCultureCategory),
			CommonCulture.of(mockLectureMap.get("KMA02106"), commonCultureCategory),
			CommonCulture.of(mockLectureMap.get("KMA02107"), commonCultureCategory),
			CommonCulture.of(mockLectureMap.get("KMA02108"), commonCultureCategory),
			CommonCulture.of(mockLectureMap.get("KMA02109"), commonCultureCategory)
		));
		String commonCultureCategoryName = commonCultureCategory.getName();
		int categoryTotalCredit = commonCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			commonCultureCategory
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(commonCultureCategoryName, false, categoryTotalCredit);
	}

	@DisplayName("영어 레벨 12: 각 카테고리의 해당하는 과목의 이수 학점을 만족하지 못한 경우 이수 미 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureCategoryFixture.class)
	void generateEng12UnCompletedCommonCultureDetailCategory(
		CommonCultureCategory commonCultureCategory,
		Set<CommonCulture> graduationLectures
	) {
		//given
		User user = UserFixture.경영학과_19학번_ENG12();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		String commonCultureCategoryName = commonCultureCategory.getName();
		int expectedTotalCredit = (commonCultureCategory == CommonCultureCategory.KOREAN) ? 0 : commonCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			commonCultureCategory
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(commonCultureCategoryName, false, expectedTotalCredit);
	}

	@DisplayName("영어 레벨 34: 각 카테고리의 해당하는 과목의 이수 학점을 만족하지 못한 경우 이수 미 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureCategoryFixture.class)
	void generateEng34UnCompletedCommonCultureDetailCategory(
		CommonCultureCategory commonCultureCategory,
		Set<CommonCulture> graduationLectures
	) {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		String commonCultureCategoryName = commonCultureCategory.getName();
		int expectedTotalCredit = (commonCultureCategory == CommonCultureCategory.KOREAN) ? 0 : commonCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			commonCultureCategory
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(commonCultureCategoryName, false, expectedTotalCredit);
	}

	@DisplayName("16~19 학번의 기독교 카테고리는 필수 과목을 수강해야 이수 완료의 카테고리 졸업 결과를 생성할 수 있다.")
	@Test
	void generateMandatorySatisfactionCommonCultureDetailCategory() {
		//given
		User user = UserFixture.경영학과_19학번_ENG12();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02106"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02107"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02108"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02109"), 2023, Semester.FIRST)

		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		Set<CommonCulture> graduationLectures = 공통교양_16_17(); // == 공통교양_18_19

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			CHRISTIAN_A
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "isSatisfiedMandatory", "totalCredits")
			.contains(CHRISTIAN_A.getName(), true, true, CHRISTIAN_A.getTotalCredit());
	}

	@DisplayName("16~19 학번의 기독교 카테고리는 필수 과목을 수강하지 않으면 수강 학점이 카테고리의 총 학점 이상이어도 이수 미완료 졸업 결과를 생성한다.")
	@Test
	void generateMandatoryUnSatisfactionCommonCultureDetailCategory() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02103"), 2023, Semester.FIRST)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		Set<CommonCulture> graduationLectures = 공통교양_16_17(); // == 공통교양_18_19

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(user, takenLectureInventory,
			graduationLectures,
			CHRISTIAN_A
		);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "isSatisfiedMandatory", "totalCredits")
			.contains(CHRISTIAN_A.getName(), false, false, CHRISTIAN_A.getTotalCredit());
	}

    @Test
    @DisplayName("KoreanLevel 이 null 인 경우 한국어 과목은 0학점으로 처리된다.")
    void koreanLevelNullShouldBeZeroCredit() {
        User user = UserFixture.한국어_레벨_NULL();

        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
        Set<CommonCulture> graduationLectures = new HashSet<>();
        DetailCategoryResult result = manager.generate(user, takenLectureInventory,
            graduationLectures, CommonCultureCategory.KOREAN);

        assertThat(result.getTotalCredits()).isEqualTo(0);
        assertThat(result.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("KoreanLevel 이 FREE 인 경우 한국어 과목은 0학점으로 처리된다.")
    void koreanLevelFreeShouldBeZeroCredit() {
        User user = UserFixture.한국어_레벨_FREE();

        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
        Set<CommonCulture> graduationLectures = new HashSet<>();
        DetailCategoryResult result = manager.generate(user, takenLectureInventory,
            graduationLectures, CommonCultureCategory.KOREAN);

        assertThat(result.getTotalCredits()).isEqualTo(0);
        assertThat(result.isCompleted()).isTrue();
    }
}
