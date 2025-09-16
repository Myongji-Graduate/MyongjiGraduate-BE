package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;                   // 추가
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase; // 추가
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace; // 추가
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;           // 오타 수정
import org.springframework.boot.test.context.TestConfiguration;                  // 추가
import org.springframework.context.annotation.Bean;                              // 추가
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // 내장 DB 대체 금지
@Import({TimetableQueryDslRepository.class, TimetableQueryRepositoryTest.TestConfig.class})
class TimetableQueryRepositoryTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @Autowired
    private TimetableQueryRepository timetableQueryRepository;

    @Autowired
    private EntityManager em;

    private TimetableJpaEntity createTimetable(
            String lectureCode, String name, String professor, String campus,
            int year, int semester, int startMinute, String classDivision
    ) {
        TimetableJpaEntity entity = TimetableJpaEntity.builder()
                .lectureCode(lectureCode)
                .classDivision(classDivision)
                .name(name)
                .credit(3)
                .campus(campus)
                .year(year)
                .semester(semester)
                .maxStudent(50)
                .koreanCode("인소102")
                .department("응용소프트웨어")
                .professor(professor)
                .startMinute1(startMinute)
                .endMinute1(startMinute + 100)
                .build();
        em.persist(entity);
        return entity;
    }

    @Test
    @DisplayName("교수명 포함 검색 (소문자/trim 적용)")
    void searchByProfessorName() {
        createTimetable("KMA001", "자료구조", "김일주", "인문", 2025, 1, 540, "001");
        createTimetable("KMA002", "운영체제", "최성운", "인문", 2025, 1, 600, "001");

        TimetableSearchConditionRequest condition = mock(TimetableSearchConditionRequest.class);
        when(condition.getProfessor()).thenReturn(" 김 ");

        List<TimetableJpaEntity> result =
                timetableQueryRepository.searchByCondition(2025, 1, CampusFilter.인문, condition);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProfessor()).isEqualTo("김일주");
    }

    @Test
    @DisplayName("교과목명 키워드 검색 (lower/trim 적용)")
    void searchByKeyword() {
        createTimetable("KMA003", "컴퓨터 네트워크", "김일주", "인문", 2025, 1, 540, "001");

        TimetableSearchConditionRequest condition = mock(TimetableSearchConditionRequest.class);
        when(condition.getKeyword()).thenReturn(" 네트워크 ");

        List<TimetableJpaEntity> result =
                timetableQueryRepository.searchByCondition(2025, 1, CampusFilter.인문, condition);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("네트워크");
    }

    @Test
    @DisplayName("캠퍼스 null이면 캠퍼스 조건 미적용")
    void searchWithoutCampus() {
        createTimetable("KMA004", "인공지능", "김주영", "자연", 2025, 1, 480, "001");

        TimetableSearchConditionRequest condition = mock(TimetableSearchConditionRequest.class);

        List<TimetableJpaEntity> result =
                timetableQueryRepository.searchByCondition(2025, 1, null, condition);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("정렬: day1 → startMinute1 → classDivision 순서")
    void searchWithSorting() {
        createTimetable("KMA005", "알고리즘", "김일주", "인문", 2025, 1, 540, "002");
        createTimetable("KMA006", "알고리즘", "김일주", "인문", 2025, 1, 540, "001");

        em.flush();
        em.clear();

        List<TimetableJpaEntity> result =
                timetableQueryRepository.searchByCondition(2025, 1, CampusFilter.인문, null);

        assertThat(result).isSortedAccordingTo((a, b) -> {
            int cmp = compareNullable(a.getDay1(), b.getDay1());
            if (cmp != 0) return cmp;
            cmp = compareNullable(a.getStartMinute1(), b.getStartMinute1());
            if (cmp != 0) return cmp;
            return a.getClassDivision().compareTo(b.getClassDivision());
        });
    }

    private <T extends Comparable<T>> int compareNullable(T a, T b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;   // nullsLast
        if (b == null) return -1;
        return a.compareTo(b);
    }
}