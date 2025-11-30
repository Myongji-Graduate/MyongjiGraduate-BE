package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TakenLectureRepositoryImplTest {

    @Mock private JPAQueryFactory jpaQueryFactory;
    @Mock private LectureCategoryResolver categoryResolver;
    @Mock private JPAQuery<Tuple> mockQuery;

    @InjectMocks private TakenLectureRepositoryImpl repository;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        // 최소 스텁: select(varargs), from, groupBy(varargs), orderBy(varargs), where/having
        lenient().when(jpaQueryFactory.select(org.mockito.ArgumentMatchers.<com.querydsl.core.types.Expression<?>[]>any()))
                .thenReturn(mockQuery);
        // repo에서 사용하는 5-arity select(id, name, credit, count, avg)
        lenient().when(jpaQueryFactory.select(
                any(Expression.class), any(Expression.class), any(Expression.class), any(Expression.class), any(Expression.class)
        )).thenReturn(mockQuery);

        // varargs 체이닝에서 Mockito 매칭 이슈 방지용 아리티 스텁
        lenient().when(mockQuery.groupBy(any(Expression.class), any(Expression.class), any(Expression.class)))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.orderBy(
                any(com.querydsl.core.types.OrderSpecifier.class),
                any(com.querydsl.core.types.OrderSpecifier.class)
        )).thenReturn(mockQuery);

        lenient().when(mockQuery.from((EntityPath<?>) any())).thenReturn(mockQuery);
        lenient().when(mockQuery.groupBy(org.mockito.ArgumentMatchers.<com.querydsl.core.types.Expression<?>[]>any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.orderBy(org.mockito.ArgumentMatchers.<com.querydsl.core.types.OrderSpecifier<?>[]>any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.where(org.mockito.ArgumentMatchers.<com.querydsl.core.types.Predicate[]>any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.having(org.mockito.ArgumentMatchers.<com.querydsl.core.types.Predicate[]>any()))
                .thenReturn(mockQuery);
        // fetch()는 각 테스트에서 given(...)으로 설정
    }

    

    @Test
    @DisplayName("getLecturesByCategory: cursor/limit 적용 후 카테고리 필터링")
    void getLecturesByCategory_appliesCursorAndFilters() {
        // given: 쿼리 결과는 비워두고 attachWithoutContext가 raw를 돌려주게 함
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        List<PopularLectureDto> raw = List.of(
                PopularLectureDto.ofWithAverage("LEC-08", "데이터베이스", 3, 50L, null, 0.0),
                PopularLectureDto.ofWithAverage("LEC-10", "선형대수", 3, 30L, null, 0.0),
                PopularLectureDto.ofWithAverage("LEC-11", "철학입문", 2, 25L, null, 0.0),
                PopularLectureDto.ofWithAverage("LEC-12", "운영체제", 3, 20L, null, 0.0),
                PopularLectureDto.ofWithAverage("LEC-13", "네트워크", 3, 15L, null, 0.0)
        );
        given(categoryResolver.attachWithoutContext(anyList())).willReturn(raw);

        List<PopularLectureDto> withCategory = List.of(
                PopularLectureDto.ofWithAverage("LEC-08", "데이터베이스", 3, 50L, PopularLectureCategory.MANDATORY_MAJOR, 0.0),
                PopularLectureDto.ofWithAverage("LEC-10", "선형대수", 3, 30L, PopularLectureCategory.MANDATORY_MAJOR, 0.0),
                PopularLectureDto.ofWithAverage("LEC-11", "철학입문", 2, 25L, PopularLectureCategory.CORE_CULTURE, 0.0),
                PopularLectureDto.ofWithAverage("LEC-12", "운영체제", 3, 20L, PopularLectureCategory.MANDATORY_MAJOR, 0.0),
                PopularLectureDto.ofWithAverage("LEC-13", "네트워크", 3, 15L, PopularLectureCategory.MANDATORY_MAJOR, 0.0)
        );
        given(categoryResolver.attachWithContext(raw, "컴퓨터공학", 2021)).willReturn(withCategory);

        // when: cursor "LEC-08" 다음부터 MANDATORY_MAJOR 카테고리만, limit 2
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "컴퓨터공학", 2021, PopularLectureCategory.MANDATORY_MAJOR, 2, "LEC-08");

        // then
        assertThat(result).extracting(PopularLectureDto::getLectureId)
                .containsExactly("LEC-10", "LEC-12", "LEC-13");
        verify(categoryResolver).attachWithContext(raw, "컴퓨터공학", 2021);
    }

    @Test
    @DisplayName("getSections: 카테고리별 개수를 집계하여 SectionMeta로 반환")
    void getSections_groupsByCategory() {
        // given
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        List<PopularLectureDto> raw = List.of(
                PopularLectureDto.ofWithAverage("A", "A1", 3, 100L, null, 0.0),
                PopularLectureDto.ofWithAverage("B", "B1", 3, 90L, null, 0.0),
                PopularLectureDto.ofWithAverage("C", "C1", 3, 80L, null, 0.0),
                PopularLectureDto.ofWithAverage("D", "D1", 3, 70L, null, 0.0)
        );
        given(categoryResolver.attachWithoutContext(anyList())).willReturn(raw);

        List<PopularLectureDto> withCtx = List.of(
                PopularLectureDto.ofWithAverage("A", "A1", 3, 100L, PopularLectureCategory.MANDATORY_MAJOR, 0.0),
                PopularLectureDto.ofWithAverage("B", "B1", 3,  90L, PopularLectureCategory.MANDATORY_MAJOR, 0.0),
                PopularLectureDto.ofWithAverage("C", "C1", 3,  80L, PopularLectureCategory.CORE_CULTURE, 0.0),
                PopularLectureDto.ofWithAverage("D", "D1", 3,  70L, PopularLectureCategory.COMMON_CULTURE, 0.0)
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
                .filter(s -> s.getCategoryName() == PopularLectureCategory.COMMON_CULTURE)
                .findFirst().orElseThrow().getTotal()).isEqualTo(1);
    }

    

    
}
