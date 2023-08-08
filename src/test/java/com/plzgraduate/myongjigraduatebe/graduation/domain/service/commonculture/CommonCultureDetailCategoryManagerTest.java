package com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import com.plzgraduate.myongjigraduatebe.fixture.CommonCultureCategoryFixture;
import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@DisplayName("각 공통교양 세부 카테고리 별 카테고리 이름, 총 학점, 이수 여부를 포함한 카테고리 졸업 결과를 생성한다.")
class CommonCultureDetailCategoryManagerTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
	CommonCultureDetailCategoryManager manager = new CommonCultureDetailCategoryManager();

	@DisplayName("각 카테고리의 해당하는 과목의 이수 학점을 만족한 경우 이수 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureCategoryFixture.class)
	void generateCompletedCommonCultureDetailCategory(CommonCultureCategory commonCultureCategory,
		Set<CommonCulture> graduationLectures) {
		//given
		User user = UserFixture.경영학과_19학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST)
		)));
		String commonCultureCategoryName = commonCultureCategory.getName();
		int categoryTotalCredit = commonCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(takenLectures, graduationLectures,
			commonCultureCategory);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(commonCultureCategoryName, true, categoryTotalCredit);
	}

	@DisplayName("각 카테고리의 해당하는 과목의 이수 학점을 만족하지 못한 경우 이수 미 완료의 카테고리 졸업 결과를 생성한다.")
	@ParameterizedTest
	@ArgumentsSource(CommonCultureCategoryFixture.class)
	void generateUnCompletedCommonCultureDetailCategory(CommonCultureCategory commonCultureCategory,
		Set<CommonCulture> graduationLectures) {
		//given
		Set<TakenLecture> takenLectures = new HashSet<>();
		String commonCultureCategoryName = commonCultureCategory.getName();
		int categoryTotalCredit = commonCultureCategory.getTotalCredit();

		//when
		DetailCategoryResult detailCategoryResult = manager.generate(takenLectures, graduationLectures,
			commonCultureCategory);

		//then
		assertThat(detailCategoryResult)
			.extracting("detailCategoryName", "isCompleted", "totalCredits")
			.contains(commonCultureCategoryName, false, categoryTotalCredit);
	}

}
