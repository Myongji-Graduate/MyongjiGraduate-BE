package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TakenLectureRepositoryImplTest {

    @Mock private JPAQueryFactory jpaQueryFactory;
    @Mock private LectureCategoryResolver categoryResolver;
    @Mock private JPAQuery<Tuple> mockQuery;

    @InjectMocks private TakenLectureRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        // 공통 QueryDSL 체이닝 스텁 (항상 호출되는 부분만 공통화)
        lenient().when(jpaQueryFactory.select((Expression<?>[]) any()))
                .thenReturn(mockQuery);
        lenient().when(mockQuery.from((EntityPath<?>) any())).thenReturn(mockQuery);
        // varargs 메서드는 배열 매처로 스텁해야 함
        lenient().when(mockQuery.groupBy((Expression<?>[]) any())).thenReturn(mockQuery);
        lenient().when(mockQuery.orderBy((OrderSpecifier<?>[]) any())).thenReturn(mockQuery);
        // fetch는 각 테스트에서 스텁
    }

    @Test
    @DisplayName("getPopularLecturesByTotalCount: resolver 반환을 그대로 돌려준다")
    void getPopularLecturesByTotalCount_returnsAttached() {
        // given: 내부 쿼리 결과는 비워두고, resolver가 반환하는 값만 검증
        given(mockQuery.fetch()).willReturn(java.util.Collections.emptyList());

        List<PopularLectureDto> attached = List.of(
                PopularLectureDto.ofWithAverage("LEC-1", "알고리즘", 3, 10L, PopularLectureCategory.MANDATORY_MAJOR, 0.0),
                PopularLectureDto.ofWithAverage("LEC-2", "자료구조", 3, 9L, PopularLectureCategory.ELECTIVE_MAJOR, 0.0)
        );
        given(categoryResolver.attachWithoutContext(anyList())).willReturn(attached);

        // when
        List<PopularLectureDto> result = repository.getPopularLecturesByTotalCount();

        // then
        assertThat(result).isEqualTo(attached);
        verify(categoryResolver).attachWithoutContext(anyList());
        verify(mockQuery).fetch();
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
                PopularLectureDto.ofWithAverage("LEC-11", "철학입문", 2, 25L, PopularLectureCategory.NORMAL_CULTURE, 0.0),
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
                PopularLectureDto.ofWithAverage("D", "D1", 3,  70L, PopularLectureCategory.NORMAL_CULTURE, 0.0)
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

    @Test
    @DisplayName("getPopularLecturesSlice: cursor/limit로 서브리스트를 반환한다")
    void getPopularLecturesSlice_returnsSubListByCursor() {
        // given: 전체 목록을 스파이로 스텁
        TakenLectureRepositoryImpl spyRepo = spy(repository);
        List<PopularLectureDto> all = List.of(
                PopularLectureDto.ofWithAverage("A", "A", 3, 100L, PopularLectureCategory.MANDATORY_MAJOR, 0.0),
                PopularLectureDto.ofWithAverage("B", "B", 3,  90L, PopularLectureCategory.MANDATORY_MAJOR, 0.0),
                PopularLectureDto.ofWithAverage("C", "C", 3,  80L, PopularLectureCategory.CORE_CULTURE, 0.0),
                PopularLectureDto.ofWithAverage("D", "D", 3,  70L, PopularLectureCategory.NORMAL_CULTURE, 0.0)
        );
        doReturn(all).when(spyRepo).getPopularLecturesByTotalCount();

        // when: limit=2, cursor=B → C, D, (limit+1이라 3개 반환)
        List<PopularLectureDto> slice = spyRepo.getPopularLecturesSlice(2, "B");

        // then
        assertThat(slice).extracting(PopularLectureDto::getLectureId)
                .containsExactly("C", "D");
    }

    @Test
    @DisplayName("getPopularLecturesByTotalCount: Tuple 매핑 null-safe 처리")
    void getPopularLecturesByTotalCount_nullSafetyOnTupleMapping() {
        // given: 단일 튜플을 반환하며, credit/total/avg는 null로 내려온다고 가정
        Tuple tuple = mock(Tuple.class);
        given(mockQuery.fetch()).willReturn(List.of(tuple));

        // id, name, credit는 Q타입으로 스텁 (repo내에서 동일 인스턴스 사용)
        var taken = com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity.takenLectureJpaEntity;
        when(tuple.get(taken.lecture.id)).thenReturn("LEC-X");
        when(tuple.get(taken.lecture.name)).thenReturn("이름X");
        when(tuple.get(taken.lecture.credit)).thenReturn(null);
        // count/avg 등은 스텁하지 않아 기본값(null) 반환되도록 둔다

        // resolver는 입력을 그대로 돌려주게 설정
        given(categoryResolver.attachWithoutContext(anyList()))
                .willAnswer(inv -> inv.getArgument(0));

        // when
        List<PopularLectureDto> result = repository.getPopularLecturesByTotalCount();

        // then: null이던 값들이 0/0L/0.0으로 안전 매핑되었는지 확인
        assertThat(result).hasSize(1);
        PopularLectureDto dto = result.get(0);
        assertThat(dto.getLectureId()).isEqualTo("LEC-X");
        assertThat(dto.getLectureName()).isEqualTo("이름X");
        assertThat(dto.getCredit()).isEqualTo(0);
        assertThat(dto.getTotalCount()).isEqualTo(0L);
        assertThat(dto.getAverageRating()).isEqualTo(0.0);
    }
}
