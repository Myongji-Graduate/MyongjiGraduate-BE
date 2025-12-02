package com.plzgraduate.myongjigraduatebe.lecture.application.service.popular;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.service.PopularLecturesService;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PopularLecturesServiceTest {

  @Mock
  private PopularLecturePort popularLecturePort;

  @InjectMocks
  private PopularLecturesService service;


  @DisplayName("카테고리 미지정 초기 진입: 섹션과 첫 카테고리 페이지를 반환한다.")
  @Test
  void getInitPopularLectures() {
    // given
    String major = "응용소프트웨어전공";
    int entryYear = 24;
    int limit = 1;
    String cursor = null;

    PopularLectureCategory first = PopularLectureCategory.CORE_CULTURE;
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
    given(popularLecturePort.getSections(major, entryYear)).willReturn(sections);

    List<PopularLectureDto> dtos = List.of(
        PopularLectureDto.ofWithAverage("LEC-10", "철학입문", 2, 50L, first, 0.0),
        PopularLectureDto.ofWithAverage("LEC-11", "심리학개론", 3, 45L, first, 0.0)
    );
    given(popularLecturePort.getLecturesByCategory(major, entryYear, first, limit, cursor))
        .willReturn(dtos);

    // when
    PopularLecturesInitResponse response = service.getInitPopularLectures(major, entryYear, limit, cursor);

    // then
    assertThat(response.getSections()).hasSize(2);
    assertThat(response.getPrimeSection().getCategoryName()).isEqualTo(first);
    assertThat(response.getPrimeSection().getLectures()).hasSize(1);
    PopularLectureResponse only = response.getPrimeSection().getLectures().getFirst();
    assertThat(only.getId()).isEqualTo("LEC-10");
    assertThat(only.getAverageRating()).isEqualTo(0.0);
    assertThat(response.getPrimeSection().getPageInfo().isHasMore()).isTrue();
    assertThat(response.getPrimeSection().getPageInfo().getNextCursor()).isEqualTo("LEC-10");
    assertThat(response.getPrimeSection().getPageInfo().getPageSize()).isEqualTo(limit);
  }

  @DisplayName("초기 섹션이 비어있으면: 빈 프라임 섹션(ALL, lectures=[], hasMore=false)")
  @Test
  void getInitPopularLectures_emptySections_returnsEmptyPrime() {
    // given
    String major = "응용소프트웨어전공";
    int entryYear = 24;
    int limit = 10;
    given(popularLecturePort.getSections(major, entryYear)).willReturn(List.of());

    // when
    PopularLecturesInitResponse response = service.getInitPopularLectures(major, entryYear, limit, null);

    // then
    assertThat(response.getSections()).isEmpty();
    assertThat(response.getPrimeSection().getCategoryName()).isEqualTo(PopularLectureCategory.ALL);
    assertThat(response.getPrimeSection().getLectures()).isEmpty();
    assertThat(response.getPrimeSection().getPageInfo().isHasMore()).isFalse();
    assertThat(response.getPrimeSection().getPageInfo().getNextCursor()).isNull();
    assertThat(response.getPrimeSection().getPageInfo().getPageSize()).isEqualTo(limit);
  }

  @DisplayName("카테고리 지정: 해당 카테고리 페이지 응답을 반환한다.")
  @Test
  void getPopularLecturesByCategory() {
    // given
    String major = "응용소프트웨어전공";
    int entryYear = 24;
    PopularLectureCategory category = PopularLectureCategory.MANDATORY_MAJOR;
    int limit = 1;
    String cursor = null;

    List<PopularLectureDto> dtos = List.of(
        PopularLectureDto.ofWithAverage("LEC-20", "운영체제", 3, 80L, category, 0.0),
        PopularLectureDto.ofWithAverage("LEC-21", "컴퓨터구조", 3, 70L, category, 0.0)
    );
    given(popularLecturePort.getLecturesByCategory(major, entryYear, category, limit, cursor))
        .willReturn(dtos);

    // when
    PopularLecturesByCategoryResponse response =
        service.getPopularLecturesByCategory(major, entryYear, category, limit, cursor);

    // then
    assertThat(response.getCategoryName()).isEqualTo(category);
    assertThat(response.getLectures()).hasSize(1);
    assertThat(response.getLectures().getFirst().getId()).isEqualTo("LEC-20");
    assertThat(response.getPageInfo().isHasMore()).isTrue();
    assertThat(response.getPageInfo().getNextCursor()).isEqualTo("LEC-20");
    assertThat(response.getPageInfo().getPageSize()).isEqualTo(limit);
  }

  @DisplayName("카테고리 지정: 데이터 없으면 NoSuchElementException")
  @Test
  void getPopularLecturesByCategory_whenEmpty_throwsNoSuchElement() {
    // given
    String major = "응용소프트웨어전공";
    int entryYear = 24;
    PopularLectureCategory category = PopularLectureCategory.MANDATORY_MAJOR;

    when(popularLecturePort.getLecturesByCategory(major, entryYear, category, 10, null))
        .thenReturn(List.of());

    // when + then
    org.assertj.core.api.Assertions.assertThatThrownBy(() ->
            service.getPopularLecturesByCategory(major, entryYear, category, 10, null)
        ).isInstanceOf(java.util.NoSuchElementException.class);
  }

  @DisplayName("기본 인기 강의: 첫 비어있지 않은 카테고리를 선택")
  @Test
  void getDefaultPopularLectures_picksFirstNonEmptyCategory() {
    // given
    String major = "응용소프트웨어전공";
    int entryYear = 24;
    int limit = 2;

    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.BASIC_ACADEMICAL_CULTURE, limit, null))
        .thenReturn(List.of());
    List<PopularLectureDto> core = List.of(
        PopularLectureDto.ofWithAverage("LEC-01", "철학", 2, 10L, PopularLectureCategory.CORE_CULTURE, 4.2),
        PopularLectureDto.ofWithAverage("LEC-02", "심리학", 2, 8L, PopularLectureCategory.CORE_CULTURE, 4.1),
        PopularLectureDto.ofWithAverage("LEC-03", "윤리학", 2, 7L, PopularLectureCategory.CORE_CULTURE, 4.0)
    );
    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.CORE_CULTURE, limit, null))
        .thenReturn(core);

    // when
    PopularLecturesByCategoryResponse resp = service.getDefaultPopularLectures(major, entryYear, limit, null);

    // then
    assertThat(resp.getCategoryName()).isEqualTo(PopularLectureCategory.CORE_CULTURE);
    assertThat(resp.getLectures()).hasSize(2);
    assertThat(resp.getLectures().getFirst().getId()).isEqualTo("LEC-01");
    assertThat(resp.getPageInfo().getNextCursor()).isEqualTo("LEC-02");
    assertThat(resp.getPageInfo().getPageSize()).isEqualTo(limit);
  }

  @DisplayName("통합 진입점: category=ALL이면 기본 로직으로 라우팅")
  @Test
  void getPopularLectures_routesAllToDefault() {
    // given
    String major = "응용소프트웨어전공";
    int entryYear = 24;
    int limit = 1;

    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.BASIC_ACADEMICAL_CULTURE, limit, null))
        .thenReturn(List.of());
    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.CORE_CULTURE, limit, null))
        .thenReturn(List.of());
    List<PopularLectureDto> list = List.of(
        PopularLectureDto.ofWithAverage("LEC-X", "데베", 3, 99L, PopularLectureCategory.COMMON_CULTURE, 0.0),
        PopularLectureDto.ofWithAverage("LEC-Y", "운영체제", 3, 88L, PopularLectureCategory.COMMON_CULTURE, 0.0)
    );
    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.COMMON_CULTURE, limit, null))
        .thenReturn(list);

    // when
    PopularLecturesByCategoryResponse resp = service.getPopularLectures(major, entryYear, PopularLectureCategory.ALL, limit, null);

    // then
    assertThat(resp.getCategoryName()).isEqualTo(PopularLectureCategory.COMMON_CULTURE);
    assertThat(resp.getLectures()).hasSize(1);
    assertThat(resp.getLectures().getFirst().getId()).isEqualTo("LEC-X");
    assertThat(resp.getPageInfo().isHasMore()).isTrue();
    assertThat(resp.getPageInfo().getNextCursor()).isEqualTo("LEC-X");
  }

  @DisplayName("통합 진입점: category≠ALL이면 byCategory 로직으로 라우팅")
  @Test
  void getPopularLectures_nonAll_routesToByCategory() {
    // given
    String major = "응용소프트웨어전공";
    int entryYear = 24;
    int limit = 2;
    PopularLectureCategory cat = PopularLectureCategory.MANDATORY_MAJOR;
    List<PopularLectureDto> list = List.of(
        PopularLectureDto.ofWithAverage("LEC-AA", "운영체제", 3, 80L, cat, 4.1),
        PopularLectureDto.ofWithAverage("LEC-AB", "컴퓨터구조", 3, 70L, cat, 4.0)
    );
    given(popularLecturePort.getLecturesByCategory(major, entryYear, cat, limit, null))
        .willReturn(list);

    // when
    PopularLecturesByCategoryResponse resp = service.getPopularLectures(major, entryYear, cat, limit, null);

    // then
    assertThat(resp.getCategoryName()).isEqualTo(cat);
    assertThat(resp.getLectures()).hasSize(2);
    assertThat(resp.getLectures().getFirst().getId()).isEqualTo("LEC-AA");
    assertThat(resp.getPageInfo().isHasMore()).isFalse();
    assertThat(resp.getPageInfo().getNextCursor()).isNull();
  }

  @DisplayName("기본 인기 강의: 전 카테고리 비어있으면 NoSuchElementException")
  @Test
  void getDefaultPopularLectures_allEmpty_throwsNoSuchElement() {
    // given
    String major = "응용소프트웨어전공";
    int entryYear = 24;
    int limit = 2;

    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.BASIC_ACADEMICAL_CULTURE, limit, null))
        .thenReturn(List.of());
    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.CORE_CULTURE, limit, null))
        .thenReturn(List.of());
    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.COMMON_CULTURE, limit, null))
        .thenReturn(List.of());
    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.MANDATORY_MAJOR, limit, null))
        .thenReturn(List.of());
    when(popularLecturePort.getLecturesByCategory(major, entryYear, PopularLectureCategory.ELECTIVE_MAJOR, limit, null))
        .thenReturn(List.of());

    // when + then
    org.assertj.core.api.Assertions.assertThatThrownBy(() ->
        service.getDefaultPopularLectures(major, entryYear, limit, null)
    )
    .isInstanceOf(java.util.NoSuchElementException.class)
    .hasMessage(ErrorCode.NON_EXISTED_LECTURE.toString());
  }

}

