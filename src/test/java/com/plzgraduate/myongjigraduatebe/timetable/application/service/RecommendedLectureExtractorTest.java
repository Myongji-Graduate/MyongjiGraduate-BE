package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateSingleDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendedLectureExtractorTest {

    @Mock
    private CalculateSingleDetailGraduationUseCase calc;

    @InjectMocks
    private RecommendedLectureExtractor sut;

    private Lecture l(String id) {
        Lecture lec = mock(Lecture.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        lenient().doReturn(id).when(lec).getId();
        return lec;
    }

    private DetailGraduationResult makeResult(List<Lecture> haveTo, List<Lecture> taken) {
        // 1) 빌더로 기본 값만 세팅해서 생성
        DetailCategoryResult dc = DetailCategoryResult.builder()
                .detailCategoryName("주전공선택")
                .isCompleted(false)
                .isSatisfiedMandatory(false)
                .totalCredits(0)
                .takenCredits(0)
                .normalLeftCredit(0)
                .freeElectiveLeftCredit(0)
                .build();

        // 2) 최종 객체의 리스트에 원하는 값 주입
        dc.getHaveToLectures().addAll(haveTo);
        dc.getTakenLectures().addAll(taken);

        // 3) 상위 Result 구성 (필요한 필드만)
        return DetailGraduationResult.builder()
                .detailCategory(List.of(dc))
                .isCompleted(false)
                .totalCredit(0)
                .graduationCategory(GraduationCategory.PRIMARY_ELECTIVE_MAJOR) // 필요시 조정
                .build();
    }

    @BeforeEach
    void setup() {
        // 테스트용 결과를 미리 만들어서 thenReturn 내부에서 추가 mocking이 일어나지 않도록 분리
        DetailGraduationResult stubResult =
                makeResult(List.of(l("A"), l("B")), List.of(l("B"), l("C")));

        lenient().when(calc.calculateSingleDetailGraduation(anyLong(), eq(GraduationCategory.PRIMARY_ELECTIVE_MAJOR)))
                .thenReturn(stubResult);
    }

    @Test
    @DisplayName("HAVE_TO 모드 → 미이수 강의 ID만 반환")
    void extractHaveTo() {
        List<String> ids = sut.extractLectureIds(1L, GraduationCategory.PRIMARY_ELECTIVE_MAJOR,
                RecommendedLectureExtractor.ExtractMode.HAVE_TO);

        assertThat(ids).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    @DisplayName("TAKEN 모드 → 이미 이수한 강의 ID만 반환")
    void extractTaken() {
        List<String> ids = sut.extractLectureIds(1L, GraduationCategory.PRIMARY_ELECTIVE_MAJOR,
                RecommendedLectureExtractor.ExtractMode.TAKEN);

        assertThat(ids).containsExactlyInAnyOrder("B", "C");
    }

    @Test
    @DisplayName("BOTH 모드 → 미이수 + 이수 강의 ID 합집합 (중복 제거)")
    void extractBoth() {
        List<String> ids = sut.extractLectureIds(1L, GraduationCategory.PRIMARY_ELECTIVE_MAJOR,
                RecommendedLectureExtractor.ExtractMode.BOTH);

        assertThat(ids).containsExactlyInAnyOrder("A", "B", "C");
    }

    @Test
    @DisplayName("DetailGraduationResult가 null이면 빈 리스트 반환")
    void nullResultReturnsEmpty() {
        when(calc.calculateSingleDetailGraduation(anyLong(), eq(GraduationCategory.CORE_CULTURE)))
                .thenReturn(null);

        List<String> ids = sut.extractLectureIds(1L, GraduationCategory.CORE_CULTURE,
                RecommendedLectureExtractor.ExtractMode.HAVE_TO);

        assertThat(ids).isEmpty();
    }

    @Test
    @DisplayName("extractRecommendedLectureIds()는 HAVE_TO 모드와 동일하게 동작")
    void extractRecommendedAlias() {
        List<String> ids = sut.extractRecommendedLectureIds(1L, GraduationCategory.PRIMARY_ELECTIVE_MAJOR);

        assertThat(ids).containsExactlyInAnyOrder("A", "B");
    }
}