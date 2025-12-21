package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PopularLecturesServiceTest {

    @Mock
    private PopularLecturePort popularLecturePort;

    @InjectMocks
    private PopularLecturesService service;

    private static PopularLectureDto dto(String id, String name, int credit, long total, PopularLectureCategory cat, double avg) {
        return PopularLectureDto.ofWithAverage(id, name, credit, total, cat, avg);
    }

    @BeforeEach
    void setUp() {
        // no-op
    }

    @Test
    @DisplayName("카테고리 지정 호출: DTO가 응답으로 매핑되고 페이지 정보가 설정된다")
    void getPopularLecturesByCategory_mapsResponseAndPage() {
        // given: 3개 -> limit=2면 hasMore=true로 잘림
        List<PopularLectureDto> dtos = List.of(
                dto("L1", "N1", 3, 5, PopularLectureCategory.CORE_CULTURE, 4.24),
                dto("L2", "N2", 2, 4, PopularLectureCategory.CORE_CULTURE, 3.91),
                dto("L3", "N3", 3, 3, PopularLectureCategory.CORE_CULTURE, 4.75)
        );
        given(popularLecturePort.getLecturesByCategory(anyString(), anyInt(), eq(PopularLectureCategory.CORE_CULTURE), anyInt(), any()))
                .willReturn(dtos);

        // when
        PopularLecturesByCategoryResponse res = service.getPopularLecturesByCategory(
                "응용소프트웨어전공", 20, PopularLectureCategory.CORE_CULTURE, 2, null
        );

        // then
        assertThat(res.getCategoryName()).isEqualTo(PopularLectureCategory.CORE_CULTURE);
        assertThat(res.getLectures()).hasSize(2);
        assertThat(res.getPageInfo().isHasMore()).isTrue();
        assertThat(res.getPageInfo().getPageSize()).isEqualTo(2);
        // 평균 반올림 1자리 확인
        assertThat(res.getLectures().getFirst().getAverageRating()).isEqualTo(4.2);
        // credit=2 매핑 확인 (두 번째 아이템이 credit=2)
        assertThat(res.getLectures().get(1).getCredit()).isEqualTo(2);
    }

    @Test
    @DisplayName("카테고리 지정 호출: 결과가 비어있으면 NON_EXISTED_LECTURE 예외")
    void getPopularLecturesByCategory_emptyThrows() {
        // given
        given(popularLecturePort.getLecturesByCategory(anyString(), anyInt(), eq(PopularLectureCategory.CORE_CULTURE), anyInt(), any()))
                .willReturn(List.of());

        // expect
        assertThatThrownBy(() -> service.getPopularLecturesByCategory(
                "응용소프트웨어전공", 20, PopularLectureCategory.CORE_CULTURE, 10, null))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(ErrorCode.NON_EXISTED_LECTURE.toString());
    }

    @Test
    @DisplayName("ALL: 고정 순서에서 첫 비어있지 않은 카테고리를 선택하여 반환한다")
    void getPopularLectures_ALL_picksFirstNonEmpty() {
        // BASIC, CORE 비어있고 COMMON에 데이터 있음
        given(popularLecturePort.getLecturesByCategory(anyString(), anyInt(), eq(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE), anyInt(), any()))
                .willReturn(List.of());
        given(popularLecturePort.getLecturesByCategory(anyString(), anyInt(), eq(PopularLectureCategory.CORE_CULTURE), anyInt(), any()))
                .willReturn(List.of());
        given(popularLecturePort.getLecturesByCategory(anyString(), anyInt(), eq(PopularLectureCategory.COMMON_CULTURE), anyInt(), any()))
                .willReturn(List.of(dto("L10", "CN", 3, 10, PopularLectureCategory.COMMON_CULTURE, 4.0)));

        PopularLecturesByCategoryResponse res = service.getPopularLectures(
                "응용소프트웨어전공", 20, PopularLectureCategory.ALL, 10, null
        );

        assertThat(res.getCategoryName()).isEqualTo(PopularLectureCategory.COMMON_CULTURE);
        assertThat(res.getLectures()).hasSize(1);
        assertThat(res.getPageInfo().isHasMore()).isFalse();
    }

    @Test
    @DisplayName("초기 호출: 섹션과 프라임 섹션을 구성한다 (첫 섹션 기준)")
    void getInitPopularLectures_buildsSections() {
        // given
        List<PopularLecturesInitResponse.SectionMeta> metas = List.of(
                PopularLecturesInitResponse.SectionMeta.builder().categoryName(PopularLectureCategory.CORE_CULTURE).total(3).build(),
                PopularLecturesInitResponse.SectionMeta.builder().categoryName(PopularLectureCategory.COMMON_CULTURE).total(1).build()
        );
        given(popularLecturePort.getSections(anyString(), anyInt())).willReturn(metas);

        List<PopularLectureDto> dtos = List.of(
                dto("L1", "N1", 3, 5, PopularLectureCategory.CORE_CULTURE, 4.11),
                dto("L2", "N2", 2, 4, PopularLectureCategory.CORE_CULTURE, 3.95) // credit=2 포함
        );
        given(popularLecturePort.getLecturesByCategory(anyString(), anyInt(), eq(PopularLectureCategory.CORE_CULTURE), anyInt(), any()))
                .willReturn(dtos);

        // when
        PopularLecturesInitResponse res = service.getInitPopularLectures(
                "응용소프트웨어전공", 20, 2, null
        );

        // then
        assertThat(res.getSections()).hasSize(2);
        assertThat(res.getPrimeSection().getCategoryName()).isEqualTo(PopularLectureCategory.CORE_CULTURE);
        assertThat(res.getPrimeSection().getLectures()).hasSize(2);
        assertThat(res.getPrimeSection().getPageInfo().isHasMore()).isFalse();
        // credit=2 포함 여부 확인
        assertThat(res.getPrimeSection().getLectures().stream().anyMatch(l -> l.getCredit() == 2)).isTrue();
    }

    @Test
    @DisplayName("초기 호출: 섹션이 비어있으면 빈 페이지를 반환한다")
    void getInitPopularLectures_emptySections() {
        given(popularLecturePort.getSections(anyString(), anyInt())).willReturn(List.of());

        PopularLecturesInitResponse res = service.getInitPopularLectures(
                "응용소프트웨어전공", 20, 10, null
        );

        assertThat(res.getSections()).isEmpty();
        assertThat(res.getPrimeSection().getLectures()).isEmpty();
        assertThat(res.getPrimeSection().getCategoryName()).isEqualTo(PopularLectureCategory.ALL);
    }
}
