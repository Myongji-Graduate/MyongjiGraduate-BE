package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimetableRepositoryTest extends PersistenceTestSupport {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private EntityManager em;

    /**
     * TimetableJpaEntity 스키마에 맞춰 필수 필드 전부 채워서 persist
     * - id는 @GeneratedValue라 세팅하지 않음
     * - (year, semester, lectureCode, classDivision) 유니크 제약을 위해 classDivision은 매번 다르게
     */
    private TimetableJpaEntity persistTimetable(long divSeed, String code, String name, String campus,
                                                int year, int semester) {
        String classDivision = String.format("%04d", divSeed % 10000);

        TimetableJpaEntity t = TimetableJpaEntity.builder()
                .classDivision(classDivision)
                .lectureCode(code)
                .name(name)
                .credit(3)
                .campus(campus)
                .year(year)
                .semester(semester)
                .maxStudent(50)
                .koreanCode("인소102")
                .department("인공지능·소프트웨어융합대학")
                .build();
        em.persist(t);
        return t;
    }

    @Test
    @DisplayName("findAllByYearAndSemester: 해당 연/학기의 전체를 반환")
    void findAllByYearAndSemester_returnsOnlyThatTerm() {
        // given
        persistTimetable(1, "KMA00001", "알고리즘", "인문", 2025, 1);
        persistTimetable(2, "KMA00002", "운영체제", "인문", 2025, 1);
        persistTimetable(3, "KMA00003", "데이터베이스", "자연", 2025, 2); // 다른 학기

        // when
        List<TimetableJpaEntity> result = timetableRepository.findAllByYearAndSemester(2025, 1);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(TimetableJpaEntity::getLectureCode)
                .containsExactlyInAnyOrder("KMA00001", "KMA00002");
    }

    @Test
    @DisplayName("findByYearAndSemesterAndNameContaining: 과목명 키워드로 필터")
    void findByYearAndSemesterAndNameContaining_filtersByNameKeyword() {
        // given (같은 학기 내에서만 검색)
        persistTimetable(1, "KMA10001", "자료구조", "인문", 2025, 2);
        persistTimetable(2, "KMA10002", "고급자료구조", "인문", 2025, 2);
        persistTimetable(3, "KMA10003", "네트워크", "자연", 2025, 2);
        // 다른 학기 동일 키워드 존재하더라도 제외
        persistTimetable(4, "KMA10004", "자료구조", "인문", 2024, 2);

        // when
        List<TimetableJpaEntity> result = timetableRepository
                .findByYearAndSemesterAndNameContaining(2025, 2, "자료구조");

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(TimetableJpaEntity::getLectureCode)
                .containsExactlyInAnyOrder("KMA10001", "KMA10002");
    }

    @Test
    @DisplayName("findByYearAndSemesterAndCampusAndLectureCodeIn: 캠퍼스와 코드 리스트로 필터")
    void findByYearAndSemesterAndCampusAndLectureCodeIn_filtersByCampusAndCodes() {
        // given
        persistTimetable(11, "KMA20001", "인공지능", "인문", 2025, 1);
        persistTimetable(12, "KMA20002", "기계학습", "인문", 2025, 1);
        persistTimetable(13, "KMA20003", "확률통계", "자연", 2025, 1); // 캠퍼스 다름
        persistTimetable(14, "KMA20004", "컴퓨터구조", "인문", 2025, 2); // 학기 다름

        List<String> codeFilter = List.of("KMA20001", "KMA20003", "KMA20004");

        // when
        List<TimetableJpaEntity> result = timetableRepository
                .findByYearAndSemesterAndCampusAndLectureCodeIn(2025, 1, "인문", codeFilter);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLectureCode()).isEqualTo("KMA20001");
    }

    @Test
    @DisplayName("findByYearAndSemesterAndLectureCodeNotIn: 지정 코드들을 제외하고 반환")
    void findByYearAndSemesterAndLectureCodeNotIn_excludesCodes() {
        // given
        persistTimetable(21, "KMA30001", "컴퓨터비전", "인문", 2025, 2);
        persistTimetable(22, "KMA30002", "컴퓨터그래픽스", "인문", 2025, 2);
        persistTimetable(23, "KMA30003", "이산수학", "자연", 2025, 2);
        // 다른 학기는 영향 없음
        persistTimetable(24, "KMA30004", "선형대수", "인문", 2025, 1);

        List<String> excludes = List.of("KMA30002");

        // when
        List<TimetableJpaEntity> result = timetableRepository
                .findByYearAndSemesterAndLectureCodeNotIn(2025, 2, excludes);

        // then
        assertThat(result)
                .extracting(TimetableJpaEntity::getLectureCode)
                .containsExactlyInAnyOrder("KMA30001", "KMA30003");
    }
}