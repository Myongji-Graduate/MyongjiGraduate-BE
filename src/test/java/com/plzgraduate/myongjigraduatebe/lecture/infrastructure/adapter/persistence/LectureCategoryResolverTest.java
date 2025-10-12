package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CoreCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LectureCategoryResolverTest {

  @Mock private MajorLectureRepository majorLectureRepository;
  @Mock private CoreCultureRepository coreCultureRepository;
  @Mock private BasicAcademicalCultureRepository basicAcademicalCultureRepository;
  @Mock private CommonCultureRepository commonCultureRepository;

  @InjectMocks private LectureCategoryResolver resolver;

  @DisplayName("컨텍스트 없이 카테고리를 부여한다.")
  @Test
  void attachWithoutContext() {
    // given
    List<PopularLectureDto> raw = List.of(
        new PopularLectureDto("A", "A", 3, 10),
        new PopularLectureDto("B", "B", 3, 9),
        new PopularLectureDto("C", "C", 3, 8),
        new PopularLectureDto("X", "X", 3, 1)
    );
    given(majorLectureRepository.findIdsByLectureIdInAndIsMandatory(anyList(), eq(1)))
        .willReturn(List.of("A"));
    given(majorLectureRepository.findIdsByLectureIdInAndIsMandatory(anyList(), eq(0)))
        .willReturn(List.of("B"));
    given(coreCultureRepository.findIdsByLectureIdIn(anyList())).willReturn(List.of("C"));
    given(basicAcademicalCultureRepository.findIdsByLectureIdIn(anyList())).willReturn(List.of());
    given(commonCultureRepository.findIdsByLectureIdIn(anyList())).willReturn(List.of());

    // when
    List<PopularLectureDto> result = resolver.attachWithoutContext(raw);

    // then
    assertThat(result).hasSize(4);
    assertThat(result.stream().filter(d -> d.getCategoryName() == PopularLectureCategory.MANDATORY_MAJOR).count()).isEqualTo(1);
    assertThat(result.stream().filter(d -> d.getCategoryName() == PopularLectureCategory.ELECTIVE_MAJOR).count()).isEqualTo(1);
    assertThat(result.stream().filter(d -> d.getCategoryName() == PopularLectureCategory.CORE_CULTURE).count()).isEqualTo(1);
    assertThat(result.stream().filter(d -> d.getCategoryName() == PopularLectureCategory.NORMAL_CULTURE).count()).isEqualTo(1);
  }

  @DisplayName("major/entryYear 컨텍스트로 카테고리를 부여한다.")
  @Test
  void attachWithContext() {
    // given
    String major = "응용소프트웨어전공"; // ICT 계열(16~24년) - College enum과 정확히 일치
    int entryYear = 20; // College enum은 2자리 연도로 비교
    List<PopularLectureDto> raw = List.of(
        new PopularLectureDto("A", "A", 3, 10), // 전필
        new PopularLectureDto("B", "B", 3, 9),  // 전선
        new PopularLectureDto("C", "C", 3, 8),  // 핵교
        new PopularLectureDto("D", "D", 3, 7),  // 공교
        new PopularLectureDto("E", "E", 3, 6)   // 학기
    );

    given(basicAcademicalCultureRepository.findIdsByLectureIdInAndCollegeIn(anyList(), any(Set.class)))
        .willReturn(List.of("E"));
    given(coreCultureRepository.findIdsByLectureIdInAndEntryYearBetween(anyList(), eq(entryYear)))
        .willReturn(List.of("C"));
    given(commonCultureRepository.findIdsByLectureIdInAndEntryYearBetween(anyList(), eq(entryYear)))
        .willReturn(List.of("D"));
    given(majorLectureRepository.findIdsByLectureIdInAndMajorsInAndIsMandatoryAndEntryYearBetween(anyList(), anyList(), eq(1), eq(entryYear)))
        .willReturn(List.of("A"));
    given(majorLectureRepository.findIdsByLectureIdInAndMajorsInAndIsMandatoryAndEntryYearBetween(anyList(), anyList(), eq(0), eq(entryYear)))
        .willReturn(List.of("B"));

    // when
    List<PopularLectureDto> result = resolver.attachWithContext(raw, major, entryYear);

    // then
    assertThat(result).hasSize(5);
    assertThat(findCategory(result, "A")).isEqualTo(PopularLectureCategory.MANDATORY_MAJOR);
    assertThat(findCategory(result, "B")).isEqualTo(PopularLectureCategory.ELECTIVE_MAJOR);
    assertThat(findCategory(result, "C")).isEqualTo(PopularLectureCategory.CORE_CULTURE);
    assertThat(findCategory(result, "D")).isEqualTo(PopularLectureCategory.COMMON_CULTURE);
    assertThat(findCategory(result, "E")).isEqualTo(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE);
  }

  private PopularLectureCategory findCategory(List<PopularLectureDto> list, String id) {
    return list.stream().filter(d -> d.getLectureId().equals(id)).findFirst().orElseThrow().getCategoryName();
  }
}
