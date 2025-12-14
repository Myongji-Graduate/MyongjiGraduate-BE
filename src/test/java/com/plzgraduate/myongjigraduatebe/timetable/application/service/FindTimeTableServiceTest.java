package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.TimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FindTimeTableServiceTest {

    @Mock private TimetablePort timetablePort;
    @Mock private FindTakenLecturePort findTakenLecturePort;
    @Mock private RecommendedLectureExtractor recommendedExtractor;
    @Mock private FindUserUseCase findUserUseCase;

    @InjectMocks private FindTimeTableService sut;

    private final int year = 2025;
    private final int semester = 2;
    private final CampusFilter campus = CampusFilter.인문; // 값은 중요치 않음

    private Timetable t(String code) {
        return mock(Timetable.class, invocation -> {
            if ("getLectureCode".equals(invocation.getMethod().getName())) {
                return code;
            }
            return org.mockito.Answers.RETURNS_DEFAULTS.answer(invocation);
        });
    }

    @BeforeEach
    void common() {
        // user는 추천카테고리 null이 아닐 때는 사용되지 않지만, 호출은 되므로 mock 반환
        User user = mock(User.class);
        when(findUserUseCase.findUserById(anyLong())).thenReturn(user);
    }

    @Test
    @DisplayName("ALL + category=null 이면 base 그대로 반환")
    void returnsBaseWhenAllAndNoCategory() {
        List<Timetable> base = List.of(t("A"), t("B"));
        when(timetablePort.findByYearAndSemester(year, semester)).thenReturn(base);

        List<Timetable> result = sut.searchCombined(1L, year, semester, campus, TakenFilter.ALL, null, null);

        assertThat(result).isSameAs(base);
        verify(timetablePort).findByYearAndSemester(year, semester);
        verifyNoInteractions(recommendedExtractor);
        verify(findTakenLecturePort, never()).findTakenLectureIdsByUserAndCodes(anyLong(), anyList());
    }

    @Test
    @DisplayName("ALL + category 지정 시: 추천∩개설 교집합만 반환 (BOTH 모드)")
    void allWithCategoryFiltersByRecommended() {
        // base: A,B,C 개설
        List<Timetable> base = List.of(t("A"), t("B"), t("C"));
        when(timetablePort.searchByCondition(eq(year), eq(semester), eq(campus), any(TimetableSearchConditionRequest.class)))
                .thenReturn(base);

        // category 지정 → extractor(BOTH)에서 B,X 추천 → 교집합은 B
        GraduationCategory cat = GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
        when(recommendedExtractor.extractLectureIds(1L, cat, RecommendedLectureExtractor.ExtractMode.BOTH))
                .thenReturn(List.of("B", "X"));

        // 최종 조회는 교집합 코드로
        Timetable onlyB = t("B");
        when(timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, campus, List.of("B")))
                .thenReturn(List.of(onlyB));

        TimetableSearchConditionRequest cond = mock(TimetableSearchConditionRequest.class);
        List<Timetable> result = sut.searchCombined(1L, year, semester, campus, TakenFilter.ALL, cond, cat);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLectureCode()).isEqualTo("B");

        // extractor 호출 모드 검증
        verify(recommendedExtractor).extractLectureIds(1L, cat, RecommendedLectureExtractor.ExtractMode.BOTH);
    }

    @Test
    @DisplayName("TAKEN: extractor(TAKEN) 결과 ∩ 개설 과목 반환")
    void takenFiltersOnlyTakenIds() {
        // base: A,B,C 개설
        List<Timetable> base = List.of(t("A"), t("B"), t("C"));
        when(timetablePort.findByYearAndSemester(year, semester)).thenReturn(base);

        GraduationCategory cat = GraduationCategory.PRIMARY_ELECTIVE_MAJOR; // 카테고리 지정해서 user 분기 회피
        when(recommendedExtractor.extractLectureIds(1L, cat, RecommendedLectureExtractor.ExtractMode.TAKEN))
                .thenReturn(List.of("A", "Z")); // 개설 교집합 → A

        when(timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, campus, List.of("A")))
                .thenReturn(List.of(t("A")));

        List<Timetable> result = sut.searchCombined(1L, year, semester, campus, TakenFilter.TAKEN, null, cat);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLectureCode()).isEqualTo("A");
        verify(findTakenLecturePort, never()).findTakenLectureIdsByUserAndCodes(anyLong(), anyList());
    }

    @Test
    @DisplayName("NOT_TAKEN: extractor(HAVE_TO)에서 이미 이수한 과목 제외")
    void notTakenExcludesAlreadyTaken() {
        // base: A,B 개설
        List<Timetable> base = List.of(t("A"), t("B"));
        when(timetablePort.findByYearAndSemester(year, semester)).thenReturn(base);

        GraduationCategory cat = GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
        when(recommendedExtractor.extractLectureIds(1L, cat, RecommendedLectureExtractor.ExtractMode.HAVE_TO))
                .thenReturn(List.of("A", "B"));

        // A는 이미 이수 → 제외, B만 남음
        when(findTakenLecturePort.findTakenLectureIdsByUserAndCodes(1L, List.of("A", "B")))
                .thenReturn(List.of("A"));

        when(timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, campus, List.of("B")))
                .thenReturn(List.of(t("B")));

        List<Timetable> result = sut.searchCombined(1L, year, semester, campus, TakenFilter.NOT_TAKEN, null, cat);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLectureCode()).isEqualTo("B");

        // taken 필터링이 호출되었는지 캡처로도 확인
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(findTakenLecturePort).findTakenLectureIdsByUserAndCodes(eq(1L), captor.capture());
        assertThat(captor.getValue()).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    @DisplayName("base가 비어있으면 빈 리스트 반환")
    void emptyBaseReturnsEmpty() {
        when(timetablePort.findByYearAndSemester(year, semester)).thenReturn(List.of());
        List<Timetable> result = sut.searchCombined(1L, year, semester, campus, TakenFilter.TAKEN, null, GraduationCategory.PRIMARY_ELECTIVE_MAJOR);
        assertThat(result).isEmpty();
        verifyNoInteractions(recommendedExtractor, findTakenLecturePort);
    }

    @Test
    @DisplayName("determineCategories: NORMAL 학생은 기본 카테고리 반환")
    void determineCategories_normalStudent() throws Exception {
        Method method = FindTimeTableService.class.getDeclaredMethod("determineCategories", Long.class);
        method.setAccessible(true);

        User normalUser = mock(User.class);
        when(normalUser.getStudentCategory()).thenReturn(com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.NORMAL);
        when(findUserUseCase.findUserById(1L)).thenReturn(normalUser);

        @SuppressWarnings("unchecked")
        List<GraduationCategory> result = (List<GraduationCategory>) method.invoke(sut, 1L);

        assertThat(result)
                .contains(GraduationCategory.PRIMARY_MANDATORY_MAJOR)
                .contains(GraduationCategory.PRIMARY_ELECTIVE_MAJOR)
                .contains(GraduationCategory.COMMON_CULTURE)
                .contains(GraduationCategory.CORE_CULTURE)
                .contains(GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE)
                .doesNotContain(GraduationCategory.SUB_MAJOR)
                .doesNotContain(GraduationCategory.DUAL_ELECTIVE_MAJOR);
    }

    @Test
    @DisplayName("determineCategories: SUB_MAJOR 학생은 SUB_MAJOR 카테고리 추가")
    void determineCategories_subMajorStudent() throws Exception {
        Method method = FindTimeTableService.class.getDeclaredMethod("determineCategories", Long.class);
        method.setAccessible(true);

        User subMajorUser = mock(User.class);
        when(subMajorUser.getStudentCategory()).thenReturn(com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.SUB_MAJOR);
        when(findUserUseCase.findUserById(1L)).thenReturn(subMajorUser);

        @SuppressWarnings("unchecked")
        List<GraduationCategory> result = (List<GraduationCategory>) method.invoke(sut, 1L);

        assertThat(result).contains(GraduationCategory.SUB_MAJOR);
    }

    @Test
    @DisplayName("determineCategories: DUAL_MAJOR 학생은 DUAL 카테고리 추가")
    void determineCategories_dualMajorStudent() throws Exception {
        Method method = FindTimeTableService.class.getDeclaredMethod("determineCategories", Long.class);
        method.setAccessible(true);

        User dualMajorUser = mock(User.class);
        when(dualMajorUser.getStudentCategory()).thenReturn(com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.DUAL_MAJOR);
        when(findUserUseCase.findUserById(1L)).thenReturn(dualMajorUser);

        @SuppressWarnings("unchecked")
        List<GraduationCategory> result = (List<GraduationCategory>) method.invoke(sut, 1L);

        assertThat(result)
                .contains(GraduationCategory.DUAL_ELECTIVE_MAJOR)
                .contains(GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE);
    }

    @Test
    @DisplayName("searchCombinedWithPagination: 페이지네이션 적용")
    void searchCombinedWithPagination_appliesPagination() {
        List<Timetable> allResults = List.of(t("A"), t("B"), t("C"), t("D"), t("E"));
        when(timetablePort.findByYearAndSemester(year, semester)).thenReturn(allResults);
        when(recommendedExtractor.extractLectureIds(anyLong(), any(), any()))
                .thenReturn(List.of("A", "B", "C", "D", "E"));
        when(timetablePort.findByYearSemesterAndLectureCodeIn(anyInt(), anyInt(), any(), anyList()))
                .thenReturn(allResults);

        FindTimetableUseCase.SearchCombinedResult result = sut.searchCombinedWithPagination(
                1L,
                new FindTimetableUseCase.SearchParams(year, semester, campus, TakenFilter.ALL, null, null),
                new FindTimetableUseCase.PaginationParams(2, 2) // 2페이지, 페이지당 2개
        );

        assertThat(result.getTotalCount()).isEqualTo(5);
        assertThat(result.getData()).hasSize(2); // 2페이지는 3,4번째 항목
    }

    @Test
    @DisplayName("searchCombinedWithPagination: 마지막 페이지 초과 시 빈 리스트 반환")
    void searchCombinedWithPagination_outOfRange() {
        List<Timetable> allResults = List.of(t("A"), t("B"));
        when(timetablePort.findByYearAndSemester(year, semester)).thenReturn(allResults);
        when(recommendedExtractor.extractLectureIds(anyLong(), any(), any()))
                .thenReturn(List.of("A", "B"));
        when(timetablePort.findByYearSemesterAndLectureCodeIn(anyInt(), anyInt(), any(), anyList()))
                .thenReturn(allResults);

        FindTimetableUseCase.SearchCombinedResult result = sut.searchCombinedWithPagination(
                1L,
                new FindTimetableUseCase.SearchParams(year, semester, campus, TakenFilter.ALL, null, null),
                new FindTimetableUseCase.PaginationParams(3, 2) // 3페이지 (존재하지 않음)
        );

        assertThat(result.getTotalCount()).isEqualTo(2);
        assertThat(result.getData()).isEmpty();
    }
}