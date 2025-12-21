package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.plzgraduate.myongjigraduatebe.core.config.JpaAuditingConfig;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({LectureCategoryResolver.class, JpaAuditingConfig.class, TakenLectureRepositoryImplTest.QuerydslConfig.class})
class TakenLectureRepositoryImplTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    PopularLecturePort repository;

    private UserJpaEntity user;

    @BeforeEach
    void setUp() {
        user = UserJpaEntity.builder()
                .authId("u1")
                .password("pw")
                .studentNumber("20200001")
                .englishLevel(EnglishLevel.BASIC)
                .studentCategory(StudentCategory.NORMAL)
                .build();
        em.persist(user);
    }

    @Test
    @DisplayName("countDistinct로 리뷰 조인 중복이 있어도 totalCount가 실제 수강기록 수로 집계된다")
    void countDistinct_preventsInflationFromReviewJoin() {
        // lectures
        LectureJpaEntity l1 = LectureJpaEntity.builder().id("L1").name("Lec1").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity l2 = LectureJpaEntity.builder().id("L2").name("Lec2").credit(3).duplicateCode(null).isRevoked(0).build();
        em.persist(l1);
        em.persist(l2);

        // CORE_CULTURE 매핑 (entryYear=20 해당되도록)
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(l1).startEntryYear(0).endEntryYear(99).build());
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(l2).startEntryYear(0).endEntryYear(99).build());

        // taken lectures: L1 x3, L2 x2
        persistTaken(l1); persistTaken(l1); persistTaken(l1);
        persistTaken(l2); persistTaken(l2);

        // reviews: L1 x5, L2 x2 (리뷰 다건으로 조인 중복 유발)
        for (int i = 0; i < 5; i++) em.persist(reviewOf("Lec1", BigDecimal.valueOf(4.5)));
        for (int i = 0; i < 2; i++) em.persist(reviewOf("Lec2", BigDecimal.valueOf(3.0)));

        em.flush();
        em.clear();

        // when
        List<PopularLectureDto> result = repository.getLecturesByCategory(
                "아무전공", 20, PopularLectureCategory.CORE_CULTURE, 10, null
        );

        // then: 정렬은 total desc, id desc
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLectureId()).isEqualTo("L1");
        assertThat(result.get(0).getTotalCount()).isEqualTo(3L);
        assertThat(result.get(1).getLectureId()).isEqualTo("L2");
        assertThat(result.get(1).getTotalCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("id-only 커서를 사용해 동률(totalCount 동일)일 때 다음 페이지 조회가 가능하다")
    void idOnlyCursorWorksWhenCountsTie() {
        // lectures with same count 1
        LectureJpaEntity a = LectureJpaEntity.builder().id("A").name("AName").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity b = LectureJpaEntity.builder().id("B").name("BName").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity c = LectureJpaEntity.builder().id("C").name("CName").credit(3).duplicateCode(null).isRevoked(0).build();
        em.persist(a); em.persist(b); em.persist(c);
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(a).startEntryYear(0).endEntryYear(99).build());
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(b).startEntryYear(0).endEntryYear(99).build());
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(c).startEntryYear(0).endEntryYear(99).build());
        persistTaken(a); persistTaken(b); persistTaken(c);
        // add reviews so join multiplies rows (should not affect counts)
        em.persist(reviewOf("AName", BigDecimal.valueOf(4.0)));
        em.persist(reviewOf("BName", BigDecimal.valueOf(4.0)));
        em.persist(reviewOf("CName", BigDecimal.valueOf(4.0)));

        em.flush();
        em.clear();

        // page 1: repository fetches limit+1 rows for hasMore check
        // limit=2 → expect 3 rows returned (C, B, A)
        List<PopularLectureDto> page1 = repository.getLecturesByCategory("x", 20, PopularLectureCategory.CORE_CULTURE, 2, null);
        assertThat(page1).hasSize(3);
        assertThat(page1.get(0).getLectureId()).isEqualTo("C");
        assertThat(page1.get(1).getLectureId()).isEqualTo("B");

        // page 2 using id-only cursor with last id from page1
        String cursor = page1.get(1).getLectureId(); // id-only 사용(트리밍 시 2번째가 마지막)
        List<PopularLectureDto> page2 = repository.getLecturesByCategory("x", 20, PopularLectureCategory.CORE_CULTURE, 2, cursor);
        assertThat(page2).hasSize(1);
        assertThat(page2.getFirst().getLectureId()).isEqualTo("A");
    }

    @Test
    @DisplayName("복합 커서(total:id) 사용 시 키셋 조건이 적용되어 다음 페이지가 조회된다")
    void compositeCursorWorks() {
        LectureJpaEntity a = LectureJpaEntity.builder().id("A").name("AName").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity b = LectureJpaEntity.builder().id("B").name("BName").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity c = LectureJpaEntity.builder().id("C").name("CName").credit(3).duplicateCode(null).isRevoked(0).build();
        em.persist(a); em.persist(b); em.persist(c);
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(a).startEntryYear(0).endEntryYear(99).build());
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(b).startEntryYear(0).endEntryYear(99).build());
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(c).startEntryYear(0).endEntryYear(99).build());
        persistTaken(a); persistTaken(b); persistTaken(c);
        em.flush();
        em.clear();

        List<PopularLectureDto> page1 = repository.getLecturesByCategory("x", 20, PopularLectureCategory.CORE_CULTURE, 2, null);
        String cursor = page1.get(1).getTotalCount() + ":" + page1.get(1).getLectureId();
        List<PopularLectureDto> page2 = repository.getLecturesByCategory("x", 20, PopularLectureCategory.CORE_CULTURE, 2, cursor);
        assertThat(page2).hasSize(1);
        assertThat(page2.getFirst().getLectureId()).isEqualTo("A");
    }

    @Test
    @DisplayName("비정상 접두어 복합 커서(abc:id)는 id-only로 폴백되어 작동한다")
    void malformedCompositeCursorFallsBackToIdOnly() {
        LectureJpaEntity a = LectureJpaEntity.builder().id("A").name("AName").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity b = LectureJpaEntity.builder().id("B").name("BName").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity c = LectureJpaEntity.builder().id("C").name("CName").credit(3).duplicateCode(null).isRevoked(0).build();
        em.persist(a); em.persist(b); em.persist(c);
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(a).startEntryYear(0).endEntryYear(99).build());
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(b).startEntryYear(0).endEntryYear(99).build());
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(c).startEntryYear(0).endEntryYear(99).build());
        persistTaken(a); persistTaken(b); persistTaken(c);
        em.flush();
        em.clear();

        List<PopularLectureDto> page1 = repository.getLecturesByCategory("x", 20, PopularLectureCategory.CORE_CULTURE, 2, null);
        String cursor = "abc:" + page1.get(1).getLectureId();
        List<PopularLectureDto> page2 = repository.getLecturesByCategory("x", 20, PopularLectureCategory.CORE_CULTURE, 2, cursor);
        assertThat(page2).hasSize(1);
        assertThat(page2.getFirst().getLectureId()).isEqualTo("A");
    }

    private void persistTaken(LectureJpaEntity lecture) {
        em.persist(TakenLectureJpaEntity.builder()
                .user(user)
                .lecture(lecture)
                .year(2020)
                .semester(Semester.FIRST)
                .build());
    }

    private LectureReviewJpaEntity reviewOf(String subject, BigDecimal rating) {
        return LectureReviewJpaEntity.builder()
                .subject(subject)
                .professor("prof")
                .semester("2020-1")
                .rating(rating)
                .content("c")
                .build();
    }

    static class QuerydslConfig {
        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }
}
