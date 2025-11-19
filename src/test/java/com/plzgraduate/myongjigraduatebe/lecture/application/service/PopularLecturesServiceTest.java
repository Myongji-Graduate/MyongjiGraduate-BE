package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.util.NoSuchElementException;

class PopularLecturesServiceTest {

    @Mock
    private PopularLecturePort port;

    private PopularLecturesService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PopularLecturesService(port);
    }

    @Test
    @DisplayName("getPopularLectures - 초기 빈 결과면 NoSuchElementException(NO_POPULAR_LECTURES)")
    void getPopularLectures_initial_empty_throws() {
        given(port.getPopularLecturesSlice(anyInt(), isNull()))
                .willReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.getPopularLectures(10, null))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("NO_POPULAR_LECTURES");
    }

    @Test
    @DisplayName("getPopularLectures - 커서 이후 빈 결과면 hasMore=false 로 페이지 반환")
    void getPopularLectures_after_cursor_empty_returns_page() {
        given(port.getPopularLecturesSlice(eq(10), anyString()))
                .willReturn(Collections.emptyList());

        PopularLecturesPageResponse resp = service.getPopularLectures(10, "CURSOR");
        assertThat(resp.getLectures()).isEmpty();
        assertThat(resp.getPageInfo().isHasMore()).isFalse();
        assertThat(resp.getPageInfo().getNextCursor()).isNull();
        assertThat(resp.getPageInfo().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("getInitPopularLectures - 섹션 총합 0이면 NoSuchElementException")
    void getInitPopularLectures_sections_total_zero_throws() {
        PopularLecturesInitResponse.SectionMeta meta = PopularLecturesInitResponse.SectionMeta.builder()
                .categoryName(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE)
                .total(0)
                .build();
        given(port.getSections(anyString(), anyInt())).willReturn(Collections.singletonList(meta));

        assertThatThrownBy(() -> service.getInitPopularLectures("컴공", 2020, 10, null))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("NO_POPULAR_LECTURES");
    }

    @Test
    @DisplayName("getInitPopularLectures - 정상 반환시 primeSection 설정 및 페이지 구성")
    void getInitPopularLectures_success() {
        PopularLecturesInitResponse.SectionMeta meta = PopularLecturesInitResponse.SectionMeta.builder()
                .categoryName(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE)
                .total(2)
                .build();
        given(port.getSections(anyString(), anyInt())).willReturn(Collections.singletonList(meta));

        PopularLectureDto dto1 = PopularLectureDto.ofWithAverage("ID1", "NAME1", 3, 100, PopularLectureCategory.BASIC_ACADEMICAL_CULTURE, 4.2);
        PopularLectureDto dto2 = PopularLectureDto.ofWithAverage("ID2", "NAME2", 3, 90, PopularLectureCategory.BASIC_ACADEMICAL_CULTURE, 4.0);
        given(port.getLecturesByCategory(anyString(), anyInt(), any(PopularLectureCategory.class), eq(10), isNull()))
                .willReturn(Arrays.asList(dto1, dto2));

        PopularLecturesInitResponse resp = service.getInitPopularLectures("컴공", 2020, 10, null);
        assertThat(resp.getSections()).hasSize(1);
        assertThat(resp.getPrimeSection().getCategoryName()).isEqualTo(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE);
        assertThat(resp.getPrimeSection().getLectures()).hasSize(2);
        assertThat(resp.getPrimeSection().getPageInfo().isHasMore()).isFalse();
    }

    @Test
    @DisplayName("getPopularLecturesByCategory - 초기 빈 결과면 NoSuchElementException")
    void getPopularLecturesByCategory_initial_empty_throws() {
        given(port.getLecturesByCategory(anyString(), anyInt(), any(PopularLectureCategory.class), anyInt(), isNull()))
                .willReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.getPopularLecturesByCategory("컴공", 2020, PopularLectureCategory.MANDATORY_MAJOR, 10, null))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("NO_POPULAR_LECTURES");
    }

    @Test
    @DisplayName("getInitPopularLectures - 섹션은 있으나 초기 primeSection 강의가 비어있으면 NoSuchElementException")
    void getInitPopularLectures_initial_prime_section_empty_throws() {
        // 섹션 총합은 0이 아니게 설정
        PopularLecturesInitResponse.SectionMeta meta = PopularLecturesInitResponse.SectionMeta.builder()
                .categoryName(PopularLectureCategory.NORMAL_CULTURE)
                .total(3)
                .build();
        given(port.getSections(anyString(), anyInt())).willReturn(Collections.singletonList(meta));

        // 초기 primeSection(첫 카테고리) 강의 결과가 비어있는 경우
        given(port.getLecturesByCategory(anyString(), anyInt(), any(PopularLectureCategory.class), anyInt(), isNull()))
                .willReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.getInitPopularLectures("컴공", 2020, 10, null))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("NO_POPULAR_LECTURES");
    }

    @Test
    @DisplayName("getPopularLectures - 커서 이후 limit+1 슬라이스면 hasMore=true, nextCursor 설정")
    void getPopularLectures_after_cursor_hasMore_true() {
        PopularLectureDto d1 = PopularLectureDto.ofWithAverage("A1", "과목A", 3, 100, PopularLectureCategory.NORMAL_CULTURE, 4.5);
        PopularLectureDto d2 = PopularLectureDto.ofWithAverage("A2", "과목B", 3, 90, PopularLectureCategory.NORMAL_CULTURE, 4.4);
        PopularLectureDto d3 = PopularLectureDto.ofWithAverage("A3", "과목C", 3, 80, PopularLectureCategory.NORMAL_CULTURE, 4.3);
        given(port.getPopularLecturesSlice(2, "CURSOR")).willReturn(Arrays.asList(d1, d2, d3));

        PopularLecturesPageResponse resp = service.getPopularLectures(2, "CURSOR");
        assertThat(resp.getLectures()).hasSize(2);
        assertThat(resp.getPageInfo().isHasMore()).isTrue();
        assertThat(resp.getPageInfo().getNextCursor()).isEqualTo("A2");
        assertThat(resp.getPageInfo().getPageSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("getPopularLecturesByCategory - 커서 이후 빈 결과면 hasMore=false, nextCursor=null")
    void getPopularLecturesByCategory_after_cursor_empty_returns_page() {
        given(port.getLecturesByCategory(anyString(), anyInt(), any(PopularLectureCategory.class), anyInt(), anyString()))
                .willReturn(Collections.emptyList());

        PopularLecturesByCategoryResponse resp = service.getPopularLecturesByCategory(
                "컴공", 2020, PopularLectureCategory.ELECTIVE_MAJOR, 5, "CURSOR");

        assertThat(resp.getCategoryName()).isEqualTo(PopularLectureCategory.ELECTIVE_MAJOR);
        assertThat(resp.getLectures()).isEmpty();
        assertThat(resp.getPageInfo().isHasMore()).isFalse();
        assertThat(resp.getPageInfo().getNextCursor()).isNull();
        assertThat(resp.getPageInfo().getPageSize()).isEqualTo(5);
    }

    @Test
    @DisplayName("getInitPopularLectures - 커서 이후 빈 결과면 hasMore=false, nextCursor=null로 정상 반환")
    void getInitPopularLectures_after_cursor_empty_returns_page() {
        // 섹션 총합은 0이 아니게 설정하여 404 조건 회피
        PopularLecturesInitResponse.SectionMeta meta = PopularLecturesInitResponse.SectionMeta.builder()
                .categoryName(PopularLectureCategory.NORMAL_CULTURE)
                .total(3)
                .build();
        given(port.getSections(anyString(), anyInt())).willReturn(Collections.singletonList(meta));

        // 커서가 있는 상태에서 강의 목록이 비어 있어도 예외가 아닌 빈 페이지 반환
        given(port.getLecturesByCategory(anyString(), anyInt(), any(PopularLectureCategory.class), anyInt(), anyString()))
                .willReturn(Collections.emptyList());

        PopularLecturesInitResponse resp = service.getInitPopularLectures("컴공", 2020, 10, "CURSOR");

        assertThat(resp.getSections()).hasSize(1);
        assertThat(resp.getPrimeSection().getCategoryName()).isEqualTo(PopularLectureCategory.NORMAL_CULTURE);
        assertThat(resp.getPrimeSection().getLectures()).isEmpty();
        assertThat(resp.getPrimeSection().getPageInfo().isHasMore()).isFalse();
        assertThat(resp.getPrimeSection().getPageInfo().getNextCursor()).isNull();
        assertThat(resp.getPrimeSection().getPageInfo().getPageSize()).isEqualTo(10);
    }
}
