package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TakenLectureRepositoryImplTest {

    @Mock private JPAQueryFactory jpaQueryFactory;
    @Mock private LectureCategoryResolver categoryResolver;
    @Mock private JPAQuery<PopularLectureDto> mockQuery;

    @InjectMocks private TakenLectureRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        // 공통 QueryDSL 체이닝 스텁 (항상 호출되는 부분만 공통화)
        lenient().when(jpaQueryFactory.select(ArgumentMatchers.<Expression<PopularLectureDto>>any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.from((EntityPath<?>) any())).thenReturn(mockQuery);
        // varargs 메서드는 배열 매처로 스텁해야 함
        lenient().when(mockQuery.groupBy((Expression<?>[]) any())).thenReturn(mockQuery);
        lenient().when(mockQuery.orderBy((OrderSpecifier<?>[]) any())).thenReturn(mockQuery);
        // limit/where는 일부 테스트에서만 사용되므로 각 테스트에서 개별 스텁
    }

    @Test
    @DisplayName("getPopularLecturesByTotalCount: 쿼리 결과에 카테고리 부착 결과를 반환")
    void getPopularLecturesByTotalCount_returnsAttached() {
        // given
        List<PopularLectureDto> raw = List.of(
                new PopularLectureDto("LEC-1", "알고리즘", 3, 10L),
                new PopularLectureDto("LEC-2", "자료구조", 3, 9L)
        );
        given(mockQuery.fetch()).willReturn(raw);

        List<PopularLectureDto> attached = List.of(
                PopularLectureDto.of("LEC-1", "알고리즘", 3, 10L, PopularLectureCategory.MANDATORY_MAJOR),
                PopularLectureDto.of("LEC-2", "자료구조", 3, 9L, PopularLectureCategory.ELECTIVE_MAJOR)
        );
        given(categoryResolver.attachWithoutContext(raw)).willReturn(attached);

        // when
        List<PopularLectureDto> result = repository.getPopularLecturesByTotalCount();

        // then
        assertThat(result).isEqualTo(attached);
        verify(categoryResolver).attachWithoutContext(raw);
        verify(mockQuery).fetch();
    }

    @Test
    @DisplayName("getLecturesByCategory: cursor가 있으면 where 적용 + 해당 카테고리만 필터")
    void getLecturesByCategory_appliesCursorAndFilters() {
        // given
        List<PopularLectureDto> raw = List.of(
                new PopularLectureDto("LEC-10", "선형대수", 3, 30L),
                new PopularLectureDto("LEC-11", "철학입문", 2, 25L),
                new PopularLectureDto("LEC-12", "운영체제", 3, 20L)
        );
        // 쿼리 체이닝 중 limit, where 사용됨
        given(mockQuery.limit(anyLong())).willReturn(mockQuery);
        given(mockQuery.where(any(Predicate.class))).willReturn(mockQuery);
        given(mockQuery.fetch()).willReturn(raw);

        List<PopularLectureDto> withCategory = List.of(
                PopularLectureDto.of("LEC-10", "선형대수", 3, 30L, PopularLectureCategory.MANDATORY_MAJOR),
                PopularLectureDto.of("LEC-11", "철학입문", 2, 25L, PopularLectureCategory.NORMAL_CULTURE),
                PopularLectureDto.of("LEC-12", "운영체제", 3, 20L, PopularLectureCategory.MANDATORY_MAJOR)
        );
        given(categoryResolver.attachWithContext(raw, "컴퓨터공학", 2021)).willReturn(withCategory);

        // when
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "컴퓨터공학", 2021, PopularLectureCategory.MANDATORY_MAJOR, 2, "LEC-00");

        // then
        assertThat(result).extracting(PopularLectureDto::getLectureId)
                .containsExactly("LEC-10", "LEC-12");
        verify(mockQuery).where(any(Predicate.class));
        verify(categoryResolver).attachWithContext(raw, "컴퓨터공학", 2021);
    }

    @Test
    @DisplayName("getSections: 카테고리별 개수를 집계하여 SectionMeta로 반환")
    void getSections_groupsByCategory() {
        // given
        List<PopularLectureDto> raw = List.of(
                new PopularLectureDto("A", "A1", 3, 100L),
                new PopularLectureDto("B", "B1", 3, 90L),
                new PopularLectureDto("C", "C1", 3, 80L),
                new PopularLectureDto("D", "D1", 3, 70L)
        );
        given(mockQuery.fetch()).willReturn(raw);
        given(categoryResolver.attachWithoutContext(raw)).willReturn(raw);

        List<PopularLectureDto> withCtx = List.of(
                PopularLectureDto.of("A", "A1", 3, 100L, PopularLectureCategory.MANDATORY_MAJOR),
                PopularLectureDto.of("B", "B1", 3,  90L, PopularLectureCategory.MANDATORY_MAJOR),
                PopularLectureDto.of("C", "C1", 3,  80L, PopularLectureCategory.CORE_CULTURE),
                PopularLectureDto.of("D", "D1", 3,  70L, PopularLectureCategory.NORMAL_CULTURE)
        );
        given(categoryResolver.attachWithContext(raw, "컴퓨터공학", 2021)).willReturn(withCtx);

        // when
        List<PopularLecturesInitResponse.SectionMeta> sections = repository.getSections("컴퓨터공학", 2021);

        // then
        assertThat(sections).hasSize(3);
        assertThat(sections.stream()
                .filter(s -> s.getCategoryName() == PopularLectureCategory.MANDATORY_MAJOR)
                .findFirst().orElseThrow().getTotal()).isEqualTo(2);
        assertThat(sections.stream()
                .filter(s -> s.getCategoryName() == PopularLectureCategory.CORE_CULTURE)
                .findFirst().orElseThrow().getTotal()).isEqualTo(1);
        assertThat(sections.stream()
                .filter(s -> s.getCategoryName() == PopularLectureCategory.NORMAL_CULTURE)
                .findFirst().orElseThrow().getTotal()).isEqualTo(1);
    }
}
