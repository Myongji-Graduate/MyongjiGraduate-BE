package com.plzgraduate.myongjigraduatebe.timetable.api;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetableControllerTest {

    @Mock
    private FindTimetableUseCase useCase;

    @InjectMocks
    private TimetableController controller;

    @Test
    @DisplayName("GET /api/v1/timetable: year/semester로 조회하고 Response로 매핑한다")
    void getByYearAndSemester_mapsResponses() {
        int year = 2025, semester = 1;

        Timetable t1 = mock(Timetable.class);
        Timetable t2 = mock(Timetable.class);
        when(useCase.findByYearAndSemester(year, semester)).thenReturn(List.of(t1, t2));

        TimetableResponse r1 = mock(TimetableResponse.class);
        TimetableResponse r2 = mock(TimetableResponse.class);

        try (MockedStatic<TimetableResponse> mocked = mockStatic(TimetableResponse.class)) {
            mocked.when(() -> TimetableResponse.from(t1)).thenReturn(r1);
            mocked.when(() -> TimetableResponse.from(t2)).thenReturn(r2);

            List<TimetableResponse> result = controller.getByYearAndSemester(year, semester);

            assertThat(result).containsExactly(r1, r2);
            verify(useCase).findByYearAndSemester(year, semester);
            mocked.verify(() -> TimetableResponse.from(t1));
            mocked.verify(() -> TimetableResponse.from(t2));
        }
    }

    @Test
    @DisplayName("GET /api/v1/timetable/search: 키워드 검색 결과를 Response로 매핑한다")
    void search_mapsResponses() {
        int year = 2025, semester = 2;
        String keyword = "프로그래밍";

        Timetable t1 = mock(Timetable.class);
        Timetable t2 = mock(Timetable.class);
        when(useCase.findByKeyword(year, semester, keyword)).thenReturn(List.of(t1, t2));

        TimetableResponse r1 = mock(TimetableResponse.class);
        TimetableResponse r2 = mock(TimetableResponse.class);

        try (MockedStatic<TimetableResponse> mocked = mockStatic(TimetableResponse.class)) {
            mocked.when(() -> TimetableResponse.from(t1)).thenReturn(r1);
            mocked.when(() -> TimetableResponse.from(t2)).thenReturn(r2);

            List<TimetableResponse> result = controller.search(year, semester, keyword);

            assertThat(result).containsExactly(r1, r2);
            verify(useCase).findByKeyword(year, semester, keyword);
            mocked.verify(() -> TimetableResponse.from(t1));
            mocked.verify(() -> TimetableResponse.from(t2));
        }
    }

    @Test
    @DisplayName("GET /api/v1/timetable/filter: 통합 검색 결과를 Response로 매핑한다")
    void combined_mapsResponses() {
        Long userId = 7L;
        int year = 2025, semester = 1;
        CampusFilter campus = CampusFilter.인문;        // null도 허용되지만 하나 골라서 테스트
        TakenFilter filter = TakenFilter.TAKEN;
        TimetableSearchConditionRequest condition = new TimetableSearchConditionRequest();
        GraduationCategory category = GraduationCategory.PRIMARY_ELECTIVE_MAJOR;

        Timetable t1 = mock(Timetable.class);
        Timetable t2 = mock(Timetable.class);
        when(useCase.searchCombined(userId, year, semester, campus, filter, condition, category))
                .thenReturn(List.of(t1, t2));

        TimetableResponse r1 = mock(TimetableResponse.class);
        TimetableResponse r2 = mock(TimetableResponse.class);

        try (MockedStatic<TimetableResponse> mocked = mockStatic(TimetableResponse.class)) {
            mocked.when(() -> TimetableResponse.from(t1)).thenReturn(r1);
            mocked.when(() -> TimetableResponse.from(t2)).thenReturn(r2);

            List<TimetableResponse> result =
                    controller.combined(userId, year, semester, campus, filter, condition, category);

            assertThat(result).containsExactly(r1, r2);
            verify(useCase).searchCombined(userId, year, semester, campus, filter, condition, category);
            mocked.verify(() -> TimetableResponse.from(t1));
            mocked.verify(() -> TimetableResponse.from(t2));
        }
    }
}