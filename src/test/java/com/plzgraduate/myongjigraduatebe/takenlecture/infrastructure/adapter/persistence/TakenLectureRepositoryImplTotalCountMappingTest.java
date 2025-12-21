package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TakenLectureRepositoryImplTotalCountMappingTest {

    @Mock
    JPAQueryFactory jpaQueryFactory;

    @Mock
    LectureCategoryResolver categoryResolver;

    @InjectMocks
    TakenLectureRepositoryImpl repository;

    @Test
    @DisplayName("getPopularLecturesByTotalCount: credit/total/avg null 분기 커버")
    void coversNullGuardsInTotalCountMapping() {
        @SuppressWarnings("unchecked")
        JPAQuery<Tuple> query = Mockito.mock(JPAQuery.class);
        Tuple tuple = Mockito.mock(Tuple.class);

        // chain: select(...)->from(...)->groupBy(...)->orderBy(...)->fetch()
        Mockito.when(jpaQueryFactory.select(
                any(Expression.class), any(Expression.class), any(Expression.class), any(Expression.class), any(Expression.class)
        )).thenReturn(query);
        Mockito.when(query.from((EntityPath<?>) any())).thenReturn(query);
        Mockito.when(query.groupBy(any(), any(), any())).thenReturn(query);
        Mockito.when(query.orderBy(any(), any())).thenReturn(query);
        Mockito.when(query.fetch()).thenReturn(List.of(tuple));

        // default all Expression lookups -> null (credit, count, avg)
        Mockito.when(tuple.get(Mockito.<Expression<?>>any())).thenReturn(null);
        // override for id/name so we have concrete values
        Mockito.when(tuple.get(QTakenLectureJpaEntity.takenLectureJpaEntity.lecture.id)).thenReturn("X");
        Mockito.when(tuple.get(QTakenLectureJpaEntity.takenLectureJpaEntity.lecture.name)).thenReturn("NameX");

        // passthrough resolver
        Mockito.when(categoryResolver.attachWithoutContext(Mockito.anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        List<PopularLectureDto> result = repository.getPopularLecturesByTotalCount();
        assertThat(result).hasSize(1);
        PopularLectureDto d = result.getFirst();
        assertThat(d.getLectureId()).isEqualTo("X");
        assertThat(d.getLectureName()).isEqualTo("NameX");
        assertThat(d.getCredit()).isZero(); // credit null -> 0
        assertThat(d.getTotalCount()).isZero(); // count null -> 0
        assertThat(d.getAverageRating()).isEqualTo(0.0); // avg null -> 0.0
    }
}

