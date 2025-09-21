package com.plzgraduate.myongjigraduatebe.lecture.application.service.popular;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.GetPopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.GetPopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.service.GetPopularLecturesService;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPopularLecturesServiceTest {

  @Mock
  private GetPopularLecturePort getPopularLecturePort;

  @InjectMocks
  private GetPopularLecturesService service;

  @DisplayName("총 수강 횟수 기준 인기 과목 목록을 그대로 반환한다.")
  @Test
  void getPopularLecturesByTotalCount() {
    // given
    List<FindPopularLectureDto> dtos = List.of(
        FindPopularLectureDto.of("LEC-1", "알고리즘", 3, 120L, PopularLectureCategory.MANDATORY_MAJOR),
        FindPopularLectureDto.of("LEC-2", "자료구조", 3, 110L, PopularLectureCategory.ELECTIVE_MAJOR)
    );
    given(getPopularLecturePort.getPopularLecturesByTotalCount()).willReturn(dtos);

    // when
    List<FindPopularLectureDto> result = service.getPopularLecturesByTotalCount();

    // then
    assertThat(result).containsExactlyElementsOf(dtos);
  }

  @DisplayName("카테고리 미지정 초기 진입: 섹션과 첫 카테고리 페이지를 반환한다.")
  @Test
  void getInitPopularLectures() {
    // given
    String major = "컴퓨터공학";
    int entryYear = 2020;
    int limit = 1;
    String cursor = null;

    PopularLectureCategory first = PopularLectureCategory.NORMAL_CULTURE;
    List<PopularLecturesInitResponse.SectionMeta> sections = List.of(
        PopularLecturesInitResponse.SectionMeta.builder()
            .categoryName(first)
            .total(2)
            .build(),
        PopularLecturesInitResponse.SectionMeta.builder()
            .categoryName(PopularLectureCategory.MANDATORY_MAJOR)
            .total(5)
            .build()
    );
    given(getPopularLecturePort.getSections(major, entryYear)).willReturn(sections);

    List<FindPopularLectureDto> dtos = List.of(
        FindPopularLectureDto.of("LEC-10", "철학입문", 2, 50L, first),
        FindPopularLectureDto.of("LEC-11", "심리학개론", 3, 45L, first)
    );
    given(getPopularLecturePort.getLecturesByCategory(major, entryYear, first, limit, cursor))
        .willReturn(dtos);

    // when
    PopularLecturesInitResponse response = service.getInitPopularLectures(major, entryYear, limit, cursor);

    // then
    assertThat(response.getSections()).hasSize(2);
    assertThat(response.getPrimeSection().getCategoryName()).isEqualTo(first);
    assertThat(response.getPrimeSection().getLectures()).hasSize(1);
    GetPopularLectureResponse only = response.getPrimeSection().getLectures().get(0);
    assertThat(only.getId()).isEqualTo("LEC-10");
    assertThat(only.getAverageRating()).isEqualTo(0.0);
    assertThat(response.getPrimeSection().getPageInfo().isHasMore()).isTrue();
    assertThat(response.getPrimeSection().getPageInfo().getNextCursor()).isEqualTo("LEC-10");
    assertThat(response.getPrimeSection().getPageInfo().getPageSize()).isEqualTo(limit);
  }

  @DisplayName("카테고리 지정: 해당 카테고리 페이지 응답을 반환한다.")
  @Test
  void getPopularLecturesByCategory() {
    // given
    String major = "컴퓨터공학";
    int entryYear = 2020;
    PopularLectureCategory category = PopularLectureCategory.MANDATORY_MAJOR;
    int limit = 1;
    String cursor = null;

    List<FindPopularLectureDto> dtos = List.of(
        FindPopularLectureDto.of("LEC-20", "운영체제", 3, 80L, category),
        FindPopularLectureDto.of("LEC-21", "컴퓨터구조", 3, 70L, category)
    );
    given(getPopularLecturePort.getLecturesByCategory(major, entryYear, category, limit, cursor))
        .willReturn(dtos);

    // when
    PopularLecturesByCategoryResponse response =
        service.getPopularLecturesByCategory(major, entryYear, category, limit, cursor);

    // then
    assertThat(response.getCategoryName()).isEqualTo(category);
    assertThat(response.getLectures()).hasSize(1);
    assertThat(response.getLectures().get(0).getId()).isEqualTo("LEC-20");
    assertThat(response.getPageInfo().isHasMore()).isTrue();
    assertThat(response.getPageInfo().getNextCursor()).isEqualTo("LEC-20");
    assertThat(response.getPageInfo().getPageSize()).isEqualTo(limit);
  }
}

