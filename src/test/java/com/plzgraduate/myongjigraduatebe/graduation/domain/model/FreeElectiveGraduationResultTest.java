package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.FREE_ELECTIVE;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CHRISTIAN_A;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.SCIENCE_TECHNOLOGY;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
                                .freeElectiveLeftCredit(3)
                                .build(),
                        DetailCategoryResult.builder()
                                .detailCategoryName(SCIENCE_TECHNOLOGY.getName())
                                .freeElectiveLeftCredit(3)
                                .build()
                ))
                .build();

        int remainCreditByTakenLectures = takenLectureInventory.getTakenLectures()
                .stream()
                .mapToInt(takenLecture -> takenLecture.getLecture()
                        .getCredit())
                .sum();
        int freeElectiveLeftCredit = detailGraduationResult.getFreeElectiveLeftCredit();
        int leftNormalCultureCredit = 5;

        //when
        FreeElectiveGraduationResult freeElectiveGraduationResult = FreeElectiveGraduationResult.create(
                7,
                takenLectureInventory, List.of(detailGraduationResult),
                leftNormalCultureCredit, user);

        //then
        assertThat(freeElectiveGraduationResult)
                .extracting("categoryName", "takenCredit")
                .contains(FREE_ELECTIVE.getName(),
                        remainCreditByTakenLectures + freeElectiveLeftCredit + leftNormalCultureCredit);
    }

    @DisplayName("봉사학점을 한 번 들었을 때 자유선택 졸업 결과를 생성한다.")
    @Test
    void createFreeElectiveGraduationResult_withOneVolunteerCredit() {
        //given
        User user = UserFixture.경영학과_19학번_ENG34();
        Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
                TakenLecture.of(user, mockLectureMap.get("KMA02198"), 2021, Semester.FIRST) // 봉사학점
        )));
        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

        int totalFreeElectiveCredit = 7;
        int leftNormalCultureCredit = 0;

        //when
        FreeElectiveGraduationResult freeElectiveGraduationResult = FreeElectiveGraduationResult.create(
                totalFreeElectiveCredit,
                takenLectureInventory,
                List.of(),
                leftNormalCultureCredit,
                user);

        //then
        assertThat(freeElectiveGraduationResult)
                .extracting("categoryName", "takenCredit")
                .contains(FREE_ELECTIVE.getName(), 1); // 봉사학점 1학점 반영
    }

    @DisplayName("봉사학점을 두 번 들었을 때 자유선택 졸업 결과를 생성한다.")
    @Test
    void createFreeElectiveGraduationResult_withTwoVolunteerCredits() {
        //given
        User user = UserFixture.경영학과_19학번_ENG34();
        Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
                TakenLecture.of(user, mockLectureMap.get("KMA02198"), 2021, Semester.FIRST), // 봉사학점 1
                TakenLecture.of(user, mockLectureMap.get("KMA02198"), 2022, Semester.SECOND) // 봉사학점 2
        )));
        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

        int totalFreeElectiveCredit = 7;
        int leftNormalCultureCredit = 0;

        //when
        FreeElectiveGraduationResult freeElectiveGraduationResult = FreeElectiveGraduationResult.create(
                totalFreeElectiveCredit,
                takenLectureInventory,
                List.of(),
                leftNormalCultureCredit,
                user);

        //then
        assertThat(freeElectiveGraduationResult)
                .extracting("categoryName", "takenCredit")
                .contains(FREE_ELECTIVE.getName(), 2); // 봉사학점 2학점 반영
    }

    @DisplayName("편입생의 자유선택 졸업 결과를 생성한다.")
    @Test
    void createFreeElectiveGraduationResult_forTransferStudent() {
        // given
        User user = UserFixture.경제학과_20학번_편입();
        user.setStudentCategory(StudentCategory.TRANSFER);
        user.setTransferCredit(new TransferCredit(51, 0, 3, 0)); // 편입 학점 설정

        // Taken lectures (총 9학점)
        Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
                TakenLecture.of(user, mockLectureMap.get("HBX01104"), 2019, Semester.FIRST),
                TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2019, Semester.FIRST),
                TakenLecture.of(user, mockLectureMap.get("HBX01106"), 2020, Semester.FIRST)
        ));
        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

        // 교양 초과분 (남은 4학점이 자유선택으로 넘어가야 함)
        DetailGraduationResult combinedCultureGraduationResult = DetailGraduationResult.builder()
                .graduationCategory(GraduationCategory.NORMAL_CULTURE)
                .detailCategory(List.of(
                        DetailCategoryResult.builder()
                                .detailCategoryName("일반교양")
                                .freeElectiveLeftCredit(4) // 초과된 통합 교양 학점
                                .build()
                ))
                .build();

        int leftNormalCultureCredit = 1;

        // when
        FreeElectiveGraduationResult freeElectiveGraduationResult = FreeElectiveGraduationResult.create(
                14,
                takenLectureInventory,
                List.of(combinedCultureGraduationResult),
                leftNormalCultureCredit,
                user
        );

        // then
        assertThat(freeElectiveGraduationResult)
                .extracting("categoryName", "takenCredit")
                .contains(FREE_ELECTIVE.getName(),
                        17); // 편입 학점 포함 계산
    }

    @DisplayName("편입생 봉사학점을 포함한 자유선택 졸업 결과를 생성한다.")
    @Test
    void createFreeElectiveGraduationResult_withVolunteerCredits_forTransferStudent() {
        // given
        User user = UserFixture.경제학과_20학번_편입();
        user.setStudentCategory(StudentCategory.TRANSFER);
        user.setTransferCredit(new TransferCredit(0, 0, 3, 0)); // 편입 학점 설정

        Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
                TakenLecture.of(user, mockLectureMap.get("KMA02198"), 2021, Semester.FIRST), // 봉사학점 1
                TakenLecture.of(user, mockLectureMap.get("KMA02198"), 2022, Semester.SECOND) // 봉사학점 2
        ));
        TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

        int totalFreeElectiveCredit = 14;
        int leftNormalCultureCredit = 0;

        // when
        FreeElectiveGraduationResult freeElectiveGraduationResult = FreeElectiveGraduationResult.create(
                totalFreeElectiveCredit,
                takenLectureInventory,
                List.of(),
                leftNormalCultureCredit,
                user
        );

        // then
        assertThat(freeElectiveGraduationResult)
                .extracting("categoryName", "takenCredit")
                .contains(FREE_ELECTIVE.getName(), 5); // 봉사학점 2 + 편입 학점 3 + 기타 계산
    }

	@DisplayName("교환학생 학점이 반영된 자유선택 졸업 결과를 생성한다.")
	@Test
	void createFreeElectiveGraduationResult_withExchangeCredits() {
		//given
		User userWithExchangeCredits = UserFixture.경제학과_18학번_교환_자율학점3학점(); // 교환학생 자유학점 3점 추가
		Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
				TakenLecture.of(userWithExchangeCredits, mockLectureMap.get("HBX01104"), 2019, Semester.FIRST), // 회계원리
				TakenLecture.of(userWithExchangeCredits, mockLectureMap.get("HBX01113"), 2019, Semester.FIRST)  // 인적자원관리
		));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		int totalFreeElectiveCredit = 7;
		int leftNormalCultureCredit = 2;

		//when
		FreeElectiveGraduationResult freeElectiveGraduationResult = FreeElectiveGraduationResult.create(
				totalFreeElectiveCredit,
				takenLectureInventory,
				List.of(),
				leftNormalCultureCredit,
				userWithExchangeCredits
		);
		freeElectiveGraduationResult.checkCompleted();
		//then
		assertThat(freeElectiveGraduationResult)
				.extracting("categoryName", "takenCredit", "isCompleted")
				.contains(FREE_ELECTIVE.getName(), 8, true); // 교환학생 학점 포함 8학점으로 졸업 요건 충족
	}
}


