package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PopularLecturesServiceTest {

    @Mock
    private PopularLecturePort popularLecturePort;

    @InjectMocks
    private PopularLecturesService popularLecturesService;

    @Test
    @DisplayName("초기 섹션 빈 데이터일 때: 에러 없이 빈 섹션/프라임 반환")
    void getInitPopularLectures_emptySections_returnsEmptyPrime() {
        // given
        when(popularLecturePort.getSections("응용소프트웨어전공", 24))
                .thenReturn(List.of());

        // when
        PopularLecturesInitResponse response = popularLecturesService.getInitPopularLectures(
                "응용소프트웨어전공", 24, 10, null
        );

        // then
        assertThat(response.getSections()).isEmpty();
        assertThat(response.getPrimeSection()).isNotNull();
        assertThat(response.getPrimeSection().getCategoryName()).isEqualTo(PopularLectureCategory.ALL);
        assertThat(response.getPrimeSection().getLectures()).isEmpty();
        assertThat(response.getPrimeSection().getPageInfo().isHasMore()).isFalse();
        assertThat(response.getPrimeSection().getPageInfo().getNextCursor()).isNull();
        assertThat(response.getPrimeSection().getPageInfo().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("카테고리 지정: 해당 카테고리 페이지 응답을 반환한다 (cursor=id)")
    void getPopularLecturesByCategory_returnsPagedWithIdCursor() {
        // given
        String major = "컴퓨터공학";
        int entryYear = 2020;
        PopularLectureCategory category = PopularLectureCategory.MANDATORY_MAJOR;
        int limit = 1;
        String cursor = null;

        List<PopularLectureDto> dtos = List.of(
                PopularLectureDto.ofWithAverage("LEC-20", "운영체제", 3, 80L, category, 0.0),
                PopularLectureDto.ofWithAverage("LEC-21", "컴퓨터구조", 3, 70L, category, 0.0)
        );
        when(popularLecturePort.getLecturesByCategory(major, entryYear, category, limit, cursor))
                .thenReturn(dtos);

        // when
        PopularLecturesByCategoryResponse response =
                popularLecturesService.getPopularLecturesByCategory(major, entryYear, category, limit, cursor);

        // then
        assertThat(response.getCategoryName()).isEqualTo(category);
        assertThat(response.getLectures()).hasSize(1);
        PopularLectureResponse first = response.getLectures().getFirst();
        assertThat(first.getId()).isEqualTo("LEC-20");
        assertThat(response.getPageInfo().isHasMore()).isTrue();
        assertThat(response.getPageInfo().getNextCursor()).isEqualTo("LEC-20");
        assertThat(response.getPageInfo().getPageSize()).isEqualTo(limit);
    }
}

