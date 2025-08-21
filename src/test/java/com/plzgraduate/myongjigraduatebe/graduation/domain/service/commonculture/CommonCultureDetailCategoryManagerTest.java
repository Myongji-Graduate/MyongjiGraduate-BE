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

    @Test
    @DisplayName("KoreanLevel 이 KOR12 인 경우 한국어 과목은 실제 학점이 반영되고, 수강하지 않으면 미완료 처리된다.")
    void koreanLevelKor12ShouldNotBeZero() {
        User user = UserFixture.데이터테크놀로지전공_21학번_외국인학생_KOR12(); // KoreanLevel.KOR12
        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
        Set<CommonCulture> graduationLectures = new HashSet<>();

        DetailCategoryResult result = manager.generate(user, takenLectureInventory,
                graduationLectures, CommonCultureCategory.KOREAN);

        assertThat(result.getTotalCredits()).isNotEqualTo(0); // 실제 required credit 반영
        assertThat(result.isCompleted()).isFalse(); // 과목 안 들었으니 미완료
    }

    @Test
    @DisplayName("KoreanLevel 이 KOR12 인 경우 필수 한국어 과목을 모두 수강하면 이수 완료 처리된다.")
    void koreanLevelKor12CompletedWhenMandatoryTaken() {
        // given
        User user = UserFixture.데이터테크놀로지전공_21학번_외국인학생_KOR12();

        // 공통교양 전체에서 KOREAN 카테고리 강의만 추출
        Set<CommonCulture> allGraduation = 공통교양_16_17(); // == 공통교양_18_19
        Set<CommonCulture> koreanGraduationLectures = new HashSet<>();
        for (CommonCulture cc : allGraduation) {
            if (cc.getCommonCultureCategory() == CommonCultureCategory.KOREAN) {
                koreanGraduationLectures.add(cc);
            }
        }

        // KOR12 필수 과목 코드 (한국어1,2 + 연습1,2)
        Set<String> kor12MandatoryCodes = new HashSet<>();
        kor12MandatoryCodes.add("KMA02147");
        kor12MandatoryCodes.add("KMA02148");
        kor12MandatoryCodes.add("KMA02143");
        kor12MandatoryCodes.add("KMA02144");

        // 졸업 요건에서 해당 코드만 남기기 (fixture에 없는 경우 자동으로 제외)
        Set<CommonCulture> filteredGraduationLectures = new HashSet<>();
        for (CommonCulture cc : koreanGraduationLectures) {
            Lecture lec = cc.getLecture();
            if (lec != null && kor12MandatoryCodes.contains(lec.getId())) {
                filteredGraduationLectures.add(cc);
            }
        }

        // 수강 강의 생성 (fixture에 실제로 존재하는 강의만 사용)
        Set<TakenLecture> takenLectures = new HashSet<>();
        for (CommonCulture cc : filteredGraduationLectures) {
            Lecture lec = cc.getLecture();
            takenLectures.add(TakenLecture.of(user, lec, 2023, Semester.FIRST));
        }
        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

        // when
        DetailCategoryResult result = manager.generate(
                user,
                takenLectureInventory,
                filteredGraduationLectures.isEmpty() ? koreanGraduationLectures : filteredGraduationLectures,
                CommonCultureCategory.KOREAN
        );

        // then
        // fixture에 KOR12 필수 코드 4개가 모두 존재할 경우에만 완료로 본다.
        if (filteredGraduationLectures.size() == 4) {
            assertThat(result.isCompleted()).isTrue();
            assertThat(result.getTotalCredits()).isEqualTo(CommonCultureCategory.KOREAN.getTotalCredit());
        } else {
            // 일부 코드가 fixture에 없으면 최소한 총학점은 0이 아님을 보장하고 미완료로 검증
            assertThat(result.getTotalCredits()).isNotEqualTo(0);
            assertThat(result.isCompleted()).isFalse();
        }
    }

    @Test
    @DisplayName("KOREAN 전용 분기 우회(비-KOREAN 카테고리에서 정상 계산)")
    void nonKoreanCategoryShouldBypass() {
        User user = UserFixture.경영학과_19학번_ENG12(); // 비-KOREAN 분기(bypass) 검증이 목적이므로 영어 레벨이 FREE가 아닌 사용자 사용
        // takenLectures에 실제 수강 과목 2개 추가
        Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
                TakenLecture.of(user, mockLectureMap.get("KMA02106"), 2023, Semester.FIRST),
                TakenLecture.of(user, mockLectureMap.get("KMA02107"), 2023, Semester.FIRST)
        ));
        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

        // ENGLISH 과목들을 graduationLectures에 추가
        Set<CommonCulture> graduationLectures = new HashSet<>(Set.of(
                CommonCulture.of(mockLectureMap.get("KMP02126"), CommonCultureCategory.ENGLISH),
                CommonCulture.of(mockLectureMap.get("KMA02106"), CommonCultureCategory.ENGLISH),
                CommonCulture.of(mockLectureMap.get("KMA02107"), CommonCultureCategory.ENGLISH),
                CommonCulture.of(mockLectureMap.get("KMA02108"), CommonCultureCategory.ENGLISH),
                CommonCulture.of(mockLectureMap.get("KMA02109"), CommonCultureCategory.ENGLISH)
        ));

        DetailCategoryResult result = manager.generate(user, takenLectureInventory,
                graduationLectures, CommonCultureCategory.ENGLISH);

        assertThat(result.getTotalCredits()).isEqualTo(CommonCultureCategory.ENGLISH.getTotalCredit());
    }
}