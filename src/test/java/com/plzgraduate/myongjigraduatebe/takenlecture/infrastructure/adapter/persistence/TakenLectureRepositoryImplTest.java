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
import org.mockito.Answers;
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
        // repo에서 사용하는 select 오버로드들
        lenient().when(jpaQueryFactory.select(
                any(Expression.class), any(Expression.class), any(Expression.class), any(Expression.class), any(Expression.class)
        )).thenReturn(mockQuery);
        lenient().when(jpaQueryFactory.select(any(Expression.class))).thenReturn(mockQuery);

        // varargs 체이닝에서 Mockito 매칭 이슈 방지용 아리티 스텁
        lenient().when(mockQuery.groupBy(any(Expression.class), any(Expression.class), any(Expression.class)))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.orderBy(
                any(com.querydsl.core.types.OrderSpecifier.class),
                any(com.querydsl.core.types.OrderSpecifier.class)
        )).thenReturn(mockQuery);
        // join/leftJoin/on/limit 체인 스텁
        lenient().when(mockQuery.join((EntityPath<?>) any())).thenReturn(mockQuery);
        lenient().when(mockQuery.leftJoin((EntityPath<?>) any())).thenReturn(mockQuery);
        lenient().when(mockQuery.on(org.mockito.ArgumentMatchers.<com.querydsl.core.types.Predicate>any())).thenReturn(mockQuery);
        lenient().when(mockQuery.limit(anyLong())).thenReturn(mockQuery);

        lenient().when(mockQuery.from((EntityPath<?>) any())).thenReturn(mockQuery);
        lenient().when(mockQuery.groupBy(org.mockito.ArgumentMatchers.<com.querydsl.core.types.Expression<?>[]>any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.orderBy(org.mockito.ArgumentMatchers.<com.querydsl.core.types.OrderSpecifier<?>[]>any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.where(org.mockito.ArgumentMatchers.<com.querydsl.core.types.Predicate[]>any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.where(any(com.querydsl.core.types.Predicate.class)))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.having(org.mockito.ArgumentMatchers.<com.querydsl.core.types.Predicate[]>any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.having(any(com.querydsl.core.types.Predicate.class)))
                .thenReturn(mockQuery);
        // fetch()는 각 테스트에서 given(...)으로 설정
    }

    

    @Test
    @DisplayName("getLecturesByCategory: DB rows 없으면 빈 결과 반환(서비스에서 404 처리)")
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

        // when: DB 키셋 결과가 없으면 빈 결과 반환(서비스에서 404 변환되므로 여기서는 빈 리스트)
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "컴퓨터공학", 2021, PopularLectureCategory.MANDATORY_MAJOR, 2, "LEC-08");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getLecturesByCategory: id-only 커서여도 결과 매핑 정상")
    @SuppressWarnings("unchecked")
    void getLecturesByCategory_idOnlyCursor_mapsRows() {
        // given: 1개 튜플을 반환하도록 구성
        Tuple tuple = mock(Tuple.class, Answers.RETURNS_DEEP_STUBS);
        given(mockQuery.fetch()).willReturn(java.util.List.of(tuple));

        // 순차 반환: id, name, credit, total (Expression 기반 get 호출 순서)
        when(tuple.get((Expression<Object>) any(Expression.class)))
                .thenReturn("LEC-20")
                .thenReturn("운영체제")
                .thenReturn(3)
                .thenReturn(80L);
        // index 기반 평균 평점
        when(tuple.get(4, Double.class)).thenReturn(4.5);

        // when
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "컴퓨터공학", 2021, PopularLectureCategory.MANDATORY_MAJOR, 1, "LEC-10");

        // then
        assertThat(result).hasSize(1);
        PopularLectureDto dto = result.getFirst();
        assertThat(dto.getLectureId()).isEqualTo("LEC-20");
        assertThat(dto.getLectureName()).isEqualTo("운영체제");
        assertThat(dto.getCredit()).isEqualTo(3);
        assertThat(dto.getTotalCount()).isEqualTo(80L);
        assertThat(dto.getAverageRating()).isEqualTo(4.5);
        assertThat(dto.getCategoryName()).isEqualTo(PopularLectureCategory.MANDATORY_MAJOR);
    }

    @Test
    @DisplayName("getLecturesByCategory: 복합 커서(total:id)도 정상 매핑")
    @SuppressWarnings("unchecked")
    void getLecturesByCategory_compositeCursor_mapsRows() {
        // given
        Tuple tuple = mock(Tuple.class, Answers.RETURNS_DEEP_STUBS);
        given(mockQuery.fetch()).willReturn(java.util.List.of(tuple));

        when(tuple.get((Expression<Object>) any(Expression.class)))
                .thenReturn("LEC-21")
                .thenReturn("컴퓨터구조")
                .thenReturn(3)
                .thenReturn(70L);
        when(tuple.get(4, Double.class)).thenReturn(4.2);

        // when
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "컴퓨터공학", 2021, PopularLectureCategory.MANDATORY_MAJOR, 1, "80:LEC-20");

        // then
        assertThat(result).hasSize(1);
        PopularLectureDto dto = result.getFirst();
        assertThat(dto.getLectureId()).isEqualTo("LEC-21");
        assertThat(dto.getLectureName()).isEqualTo("컴퓨터구조");
        assertThat(dto.getCredit()).isEqualTo(3);
        assertThat(dto.getTotalCount()).isEqualTo(70L);
        assertThat(dto.getAverageRating()).isEqualTo(4.2);
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

    @Test
    @DisplayName("getSections: 총합 기준 조회 매핑 로직(튜플→DTO) 커버")
    @SuppressWarnings("unchecked")
    void getSections_coversTupleMappingInTotalCountQuery() {
        // given: 총합 기준 쿼리에서 1개 튜플 반환 -> 매핑 로직 실행
        Tuple t = mock(Tuple.class, Answers.RETURNS_DEEP_STUBS);
        given(mockQuery.fetch()).willReturn(java.util.List.of(t));
        when(t.get(any(Expression.class)))
                .thenReturn("ID1")
                .thenReturn("이름1")
                .thenReturn(3)
                .thenReturn(10L)
                .thenReturn(4.0);

        // 매핑 이후 attachWithoutContext/attachWithContext가 정상 동작하도록 스텁
        List<PopularLectureDto> raw = List.of(
                PopularLectureDto.ofWithAverage("ID1", "이름1", 3, 10L, null, 4.0)
        );
        given(categoryResolver.attachWithoutContext(anyList())).willReturn(raw);

        List<PopularLectureDto> withCtx = List.of(
                PopularLectureDto.ofWithAverage("ID1", "이름1", 3, 10L, PopularLectureCategory.MANDATORY_MAJOR, 4.0)
        );
        given(categoryResolver.attachWithContext(raw, "컴퓨터공학", 2021)).willReturn(withCtx);

        // when
        List<PopularLecturesInitResponse.SectionMeta> sections = repository.getSections("컴퓨터공학", 2021);

        // then: 1개 카테고리만 1건으로 집계
        assertThat(sections).hasSize(1);
        assertThat(sections.getFirst().getCategoryName()).isEqualTo(PopularLectureCategory.MANDATORY_MAJOR);
        assertThat(sections.getFirst().getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getLecturesByCategory: 잘못된 복합 커서(문자 prefix) → id-only로 폴백")
    @SuppressWarnings("unchecked")
    void getLecturesByCategory_invalidCompositeCursor_fallsBackToIdOnly() {
        // given: 튜플 1개 매핑
        Tuple tuple = mock(Tuple.class, Answers.RETURNS_DEEP_STUBS);
        given(mockQuery.fetch()).willReturn(java.util.List.of(tuple));
        when(tuple.get((Expression<Object>) any(Expression.class)))
                .thenReturn("LEC-30")
                .thenReturn("알고리즘")
                .thenReturn(3)
                .thenReturn(60L);
        when(tuple.get(4, Double.class)).thenReturn(4.3);

        // when
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "컴퓨터공학", 2021, PopularLectureCategory.MANDATORY_MAJOR, 1, "abc:LEC-20");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getLectureId()).isEqualTo("LEC-30");
    }

    @Test
    @DisplayName("applyCategoryJoin: CORE_CULTURE 케이스 경로 커버")
    void applyCategoryJoin_coreCulture_path() {
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        List<PopularLectureDto> res = repository.getLecturesByCategory(
                "컴", 2021, PopularLectureCategory.CORE_CULTURE, 1, null);
        assertThat(res).isEmpty();
    }

    @Test
    @DisplayName("applyCategoryJoin: COMMON_CULTURE 케이스 경로 커버")
    void applyCategoryJoin_commonCulture_path() {
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        List<PopularLectureDto> res = repository.getLecturesByCategory(
                "컴", 2021, PopularLectureCategory.COMMON_CULTURE, 1, null);
        assertThat(res).isEmpty();
    }

    @Test
    @DisplayName("applyCategoryJoin: BASIC_ACADEMICAL_CULTURE 케이스 경로 커버")
    void applyCategoryJoin_basicAcademical_path() {
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        // College 매핑에 존재하는 전공/입학년도 사용(예: 응용소프트웨어전공, 24)
        List<PopularLectureDto> res = repository.getLecturesByCategory(
                "응용소프트웨어전공", 24, PopularLectureCategory.BASIC_ACADEMICAL_CULTURE, 1, null);
        assertThat(res).isEmpty();
    }

    @Test
    @DisplayName("applyCategoryJoin: MANDATORY_MAJOR 케이스 경로 커버")
    void applyCategoryJoin_mandatory_path() {
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        List<PopularLectureDto> res = repository.getLecturesByCategory(
                "컴", 2021, PopularLectureCategory.MANDATORY_MAJOR, 1, null);
        assertThat(res).isEmpty();
    }

    @Test
    @DisplayName("applyCategoryJoin: ELECTIVE_MAJOR 케이스 경로 커버")
    void applyCategoryJoin_elective_path() {
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        List<PopularLectureDto> res = repository.getLecturesByCategory(
                "컴", 2021, PopularLectureCategory.ELECTIVE_MAJOR, 1, null);
        assertThat(res).isEmpty();
    }

    @Test
    @DisplayName("applyCategoryJoin: 지원하지 않는 카테고리(ALL) → IllegalArgumentException")
    void applyCategoryJoin_unsupported_all_throws() {
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                repository.getLecturesByCategory("컴", 2021, PopularLectureCategory.ALL, 1, null)
        ).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    @DisplayName("getSections: 튜플 매핑에서 null 값들은 기본값(0,0L,0.0)으로 변환")
    @SuppressWarnings("unchecked")
    void getSections_tupleMapping_nulls_defaultValues() {
        // given: 튜플의 credit/total/avg가 null로 들어오는 경우
        Tuple t = mock(Tuple.class, Answers.RETURNS_DEEP_STUBS);
        given(mockQuery.fetch()).willReturn(java.util.List.of(t));
        when(t.get(any(Expression.class)))
                .thenReturn("ID2")
                .thenReturn("이름2")
                .thenReturn(null)    // credit
                .thenReturn(null)    // total
                .thenReturn(null);   // avg

        // attachWithoutContext에 전달된 값을 캡처해서 기본값이 세팅되었는지 확인
        org.mockito.ArgumentCaptor<java.util.List<PopularLectureDto>> captor =
                org.mockito.ArgumentCaptor.forClass(java.util.List.class);
        given(categoryResolver.attachWithoutContext(captor.capture()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // attachWithContext는 입력값 그대로 반환(카테고리 붙이지 않음)
        given(categoryResolver.attachWithContext(anyList(), anyString(), anyInt()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        repository.getSections("컴퓨터공학", 2021);

        // then: 캡처된 DTO의 기본값 확인
        PopularLectureDto dto = captor.getValue().getFirst();
        assertThat(dto.getCredit()).isEqualTo(0);
        assertThat(dto.getTotalCount()).isEqualTo(0L);
        assertThat(dto.getAverageRating()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("getSections: category=null 항목은 집계에서 제외")
    void getSections_filtersNullCategory() {
        // given
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());
        List<PopularLectureDto> raw = List.of(
                PopularLectureDto.ofWithAverage("A", "A1", 3, 100L, null, 0.0)
        );
        given(categoryResolver.attachWithoutContext(anyList())).willReturn(raw);

        // withContext에 null 카테고리와 유효 카테고리를 섞어서 반환
        List<PopularLectureDto> withCtx = List.of(
                PopularLectureDto.ofWithAverage("A", "A1", 3, 100L, null, 0.0),
                PopularLectureDto.ofWithAverage("B", "B1", 3,  90L, PopularLectureCategory.MANDATORY_MAJOR, 0.0)
        );
        given(categoryResolver.attachWithContext(raw, "컴퓨터공학", 2021)).willReturn(withCtx);

        // when
        List<PopularLecturesInitResponse.SectionMeta> sections = repository.getSections("컴퓨터공학", 2021);

        // then: null 카테고리는 제외되어 1개만 집계
        assertThat(sections).hasSize(1);
        assertThat(sections.getFirst().getCategoryName()).isEqualTo(PopularLectureCategory.MANDATORY_MAJOR);
        assertThat(sections.getFirst().getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getLecturesByCategory: toDto가 null 값들을 기본값으로 변환")
    @SuppressWarnings("unchecked")
    void getLecturesByCategory_nulls_defaults() {
        // given
        Tuple tuple = mock(Tuple.class, Answers.RETURNS_DEEP_STUBS);
        given(mockQuery.fetch()).willReturn(java.util.List.of(tuple));

        when(tuple.get((Expression<Object>) any(Expression.class)))
                .thenReturn("LEC-99")
                .thenReturn("임베디드")
                .thenReturn(null)   // credit
                .thenReturn(null);  // total
        when(tuple.get(4, Double.class)).thenReturn(null); // avg

        // when
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "컴퓨터공학", 2021, PopularLectureCategory.MANDATORY_MAJOR, 1, null);

        // then
        PopularLectureDto dto = result.getFirst();
        assertThat(dto.getCredit()).isEqualTo(0);
        assertThat(dto.getTotalCount()).isEqualTo(0L);
        assertThat(dto.getAverageRating()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("getLecturesByCategory: cursor가 공백이면 파싱 null 처리(키셋 미적용)")
    void getLecturesByCategory_blankCursor_treatedAsNull() {
        // given
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());

        // when: 공백 커서
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "컴퓨터공학", 2021, PopularLectureCategory.MANDATORY_MAJOR, 1, "   ");

        // then: 예외 없이 정상 빈 리스트
        assertThat(result).isEmpty();
    }

    
}
