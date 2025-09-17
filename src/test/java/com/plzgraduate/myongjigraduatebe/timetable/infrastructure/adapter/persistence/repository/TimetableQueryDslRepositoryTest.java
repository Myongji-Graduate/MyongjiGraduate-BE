package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TimetableQueryDslRepositoryTest.TestConfig.class, TimetableQueryDslRepository.class})
class TimetableQueryDslRepositoryTest extends PersistenceTestSupport {

    @TestConfiguration
    static class TestConfig {
        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @org.springframework.beans.factory.annotation.Autowired
    private EntityManager em;

    @org.springframework.beans.factory.annotation.Autowired
    private TimetableQueryRepository repository;

    // ============ helper ============
    private TimetableJpaEntity tt(int year, int semester, String campus,
                                  String lectureCode, String classDivision,
                                  String name, String professor,
                                  String day1, Integer startMinute1) {
        TimetableJpaEntity e = TimetableJpaEntity.builder()
                .year(year)
                .semester(semester)
                .campus(campus)
                .lectureCode(lectureCode)
                .classDivision(classDivision)
                .name(name)
                .credit(3)
                .department("응용소프트웨어")
                .koreanCode("인소102")
                .maxStudent(50)
                .professor(professor)
                .day1(day1)
                .startMinute1(startMinute1)
                .build();
        em.persist(e);
        return e;
    }

    @Test
    @DisplayName("캠퍼스 미지정: year/semester만으로 전체 조회 + 정렬(day1 -> startMinute1 -> classDivision)")
    void searchWithoutCampus() {
        tt(2025, 1, "인문", "KMA10001", "0001", "알고리즘", "김일주", "월요일", 540);
        tt(2025, 1, "자연", "KMA10002", "0001", "자료구조", "최성운", "화요일", 600);
        tt(2024, 2, "인문", "KMA99999", "0001", "무시", "김주영", "월요일", 540);

        List<TimetableJpaEntity> result =
                repository.searchByCondition(2025, 1, null, null);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TimetableJpaEntity::getDay1)
                .containsExactly("월요일", "화요일");
    }

    @Test
    @DisplayName("교수명(대소문자 무시) + 캠퍼스 필터: professor like + campus = 인문")
    void searchByProfessorName() {
        tt(2025, 1, "인문", "KMA20001", "0001", "프로그래밍", "김일주", "월요일", 540);
        tt(2025, 1, "인문", "KMA20002", "0001", "프로그래밍", "최성운", "화요일", 600);
        tt(2025, 1, "자연", "KMA20003", "0001", "프로그래밍", "김주영", "화요일", 600);

        TimetableSearchConditionRequest cond = new TimetableSearchConditionRequest();
        cond.setProfessor("김"); // ← 한글로 맞춤

        List<TimetableJpaEntity> result =
                repository.searchByCondition(2025, 1, CampusFilter.인문, cond);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProfessor()).isEqualTo("김일주"); // ← 기대값 수정
        assertThat(result.get(0).getCampus()).isEqualTo("인문");
    }

    @Test
    @DisplayName("과목명 키워드(대소문자 무시) + 캠퍼스 필터: name like + campus = 인문")
    void searchByKeyword() {
        tt(2025, 1, "인문", "KMA30001", "0001", "기초프로그래밍", "김일주", "월요일", 540);
        tt(2025, 1, "인문", "KMA30002", "0001", "자료구조", "최성운", "월요일", 600);
        tt(2025, 1, "자연", "KMA30003", "0001", "기초프로그래밍", "김주영", "월요일", 600);

        TimetableSearchConditionRequest cond = new TimetableSearchConditionRequest();
        cond.setKeyword("프로그래밍");

        List<TimetableJpaEntity> result =
                repository.searchByCondition(2025, 1, CampusFilter.인문, cond);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLectureCode()).isEqualTo("KMA30001");
    }

    @Test
    @DisplayName("정렬: day1 -> startMinute1 -> classDivision, null은 뒤로(Nulls last)")
    void searchWithSorting() {
        tt(2025, 1, "인문", "KMA40001", "0002", "A", "김일주", "월요일", 540);
        tt(2025, 1, "인문", "KMA40002", "0001", "B", "최성운", "월요일", 600);
        tt(2025, 1, "인문", "KMA40003", "0001", "C", "김주영", null, null);

        List<TimetableJpaEntity> result =
                repository.searchByCondition(2025, 1, CampusFilter.인문, null);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getLectureCode()).isEqualTo("KMA40001");
        assertThat(result.get(1).getLectureCode()).isEqualTo("KMA40002");
        assertThat(result.get(2).getLectureCode()).isEqualTo("KMA40003");
    }
}
