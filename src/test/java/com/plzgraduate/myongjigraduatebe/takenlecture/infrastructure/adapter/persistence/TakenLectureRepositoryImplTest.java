package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
    @DisplayName("총 인기 강의 조회: subquery 평균 평점 + 전체 수강 카운트가 계산된다")
    void getPopularLecturesByTotalCount_coversAvgSubquery() {
        // lectures
        LectureJpaEntity l1 = LectureJpaEntity.builder().id("TL1").name("TLec1").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity l2 = LectureJpaEntity.builder().id("TL2").name("TLec2").credit(2).duplicateCode(null).isRevoked(0).build();
        em.persist(l1); em.persist(l2);

        // taken counts: l1 x3, l2 x2
        persistTaken(l1); persistTaken(l1); persistTaken(l1);
        persistTaken(l2); persistTaken(l2);

        // reviews for subquery avg (subject = lecture.name)
        em.persist(reviewOf("TLec1", BigDecimal.valueOf(4.0)));
        em.persist(reviewOf("TLec1", BigDecimal.valueOf(5.0))); // avg 4.5
        em.persist(reviewOf("TLec2", BigDecimal.valueOf(3.0))); // avg 3.0

        em.flush();
        em.clear();

        List<PopularLectureDto> list = repository.getPopularLecturesByTotalCount();

        PopularLectureDto d1 = list.stream().filter(d -> d.getLectureId().equals("TL1")).findFirst().orElseThrow();
        PopularLectureDto d2 = list.stream().filter(d -> d.getLectureId().equals("TL2")).findFirst().orElseThrow();

        assertThat(d1.getTotalCount()).isEqualTo(3L);
        assertThat(d1.getAverageRating()).isEqualTo(4.5);
        assertThat(d1.getCredit()).isEqualTo(3);

        assertThat(d2.getTotalCount()).isEqualTo(2L);
        assertThat(d2.getAverageRating()).isEqualTo(3.0);
        assertThat(d2.getCredit()).isEqualTo(2);
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

    @Test
    @DisplayName("COMMON_CULTURE 카테고리 조회 + 빈 문자열 커서 처리")
    void commonCultureQuery_withBlankCursor() {
        LectureJpaEntity com = LectureJpaEntity.builder().id("COM1").name("Common1").credit(2).duplicateCode(null).isRevoked(0).build();
        em.persist(com);
        // CommonCulture 매핑 (entryYear 포함)
        em.persist(CommonCultureJpaEntity
                .builder()
                .lectureJpaEntity(com)
                .commonCultureCategory(CommonCultureCategory.KOREAN)
                .startEntryYear(0)
                .endEntryYear(99)
                .build());
        // 수강 기록 1건
        persistTaken(com);
        em.flush();
        em.clear();

        List<PopularLectureDto> page = repository.getLecturesByCategory("x", 20, PopularLectureCategory.COMMON_CULTURE, 10, "");
        assertThat(page).hasSize(1);
        assertThat(page.getFirst().getLectureId()).isEqualTo("COM1");
        assertThat(page.getFirst().getCredit()).isEqualTo(2);
    }

    @Test
    @DisplayName("섹션 메타: 데이터가 없으면 빈 리스트 반환")
    void getSections_emptyReturnsEmpty() {
        var sections = repository.getSections("응용소프트웨어전공", 20);
        assertThat(sections).isEmpty();
    }

    @Test
    @DisplayName("BASIC_ACADEMICAL_CULTURE 카테고리 조회가 단과대 매핑으로 동작한다")
    void basicAcademicalCultureQuery() {
        String major = "응용소프트웨어전공";
        int entryYear = 20;
        String college = com.plzgraduate.myongjigraduatebe.user.domain.model.College
                .findBelongingCollege(major, entryYear).getName();

        LectureJpaEntity basic = LectureJpaEntity.builder().id("BASIC1").name("BasicName").credit(2).duplicateCode(null).isRevoked(0).build();
        em.persist(basic);
        em.persist(com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity
                .builder()
                .lectureJpaEntity(basic)
                .college(college)
                .build());
        persistTaken(basic);
        em.flush();
        em.clear();

        List<PopularLectureDto> res = repository.getLecturesByCategory(major, entryYear, PopularLectureCategory.BASIC_ACADEMICAL_CULTURE, 10, null);
        assertThat(res).hasSize(1);
        assertThat(res.getFirst().getLectureId()).isEqualTo("BASIC1");
        assertThat(res.getFirst().getCredit()).isEqualTo(2);
    }

    @Test
    @DisplayName("MANDATORY_MAJOR 카테고리 조회가 전공+년도+mandatory=1 매핑으로 동작한다")
    void mandatoryMajorQuery() {
        String major = "응용소프트웨어전공";
        int entryYear = 20;
        LectureJpaEntity mand = LectureJpaEntity.builder().id("MAND1").name("MandName").credit(3).duplicateCode(null).isRevoked(0).build();
        em.persist(mand);
        em.persist(com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity.builder()
                .lectureJpaEntity(mand)
                .major(major)
                .mandatory(1)
                .startEntryYear(0)
                .endEntryYear(99)
                .build());
        persistTaken(mand);
        em.flush();
        em.clear();

        List<PopularLectureDto> res = repository.getLecturesByCategory(major, entryYear, PopularLectureCategory.MANDATORY_MAJOR, 10, null);
        assertThat(res).hasSize(1);
        assertThat(res.getFirst().getLectureId()).isEqualTo("MAND1");
    }

    @Test
    @DisplayName("ELECTIVE_MAJOR 카테고리 조회가 전공+년도+mandatory=0 매핑으로 동작한다")
    void electiveMajorQuery() {
        String major = "응용소프트웨어전공";
        int entryYear = 20;
        LectureJpaEntity elect = LectureJpaEntity.builder().id("ELE2").name("ElectName").credit(3).duplicateCode(null).isRevoked(0).build();
        em.persist(elect);
        em.persist(com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity.builder()
                .lectureJpaEntity(elect)
                .major(major)
                .mandatory(0)
                .startEntryYear(0)
                .endEntryYear(99)
                .build());
        persistTaken(elect);
        em.flush();
        em.clear();

        List<PopularLectureDto> res = repository.getLecturesByCategory(major, entryYear, PopularLectureCategory.ELECTIVE_MAJOR, 10, null);
        assertThat(res).hasSize(1);
        assertThat(res.getFirst().getLectureId()).isEqualTo("ELE2");
    }

    @Test
    @DisplayName("ALL 카테고리로 조회 시 Unsupported category 예외")
    void unsupportedCategoryThrows() {
        String major = "응용소프트웨어전공";
        int entryYear = 20;
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                repository.getLecturesByCategory(major, entryYear, PopularLectureCategory.ALL, 10, null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported category: ALL");
    }

    @Test
    @DisplayName("섹션 메타: 카테고리별 개수를 그룹핑하고 고정 순서로 반환한다")
    void getSections_groupsByCategoryAndOrder() {
        // given: major/entryYear에 맞는 맥락
        String major = "응용소프트웨어전공";
        int entryYear = 20;

        // lectures by category
        LectureJpaEntity basic = LectureJpaEntity.builder().id("BAS1").name("Basic1").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity core1 = LectureJpaEntity.builder().id("COR1").name("Core1").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity core2 = LectureJpaEntity.builder().id("COR2").name("Core2").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity mand = LectureJpaEntity.builder().id("MAN1").name("Mand1").credit(3).duplicateCode(null).isRevoked(0).build();
        LectureJpaEntity elect = LectureJpaEntity.builder().id("ELE1").name("Elect1").credit(3).duplicateCode(null).isRevoked(0).build();
        // unmapped lecture (categoryName=null)
        LectureJpaEntity other = LectureJpaEntity.builder().id("OTH1").name("Other1").credit(1).duplicateCode(null).isRevoked(0).build();
        em.persist(basic); em.persist(core1); em.persist(core2); em.persist(mand); em.persist(elect); em.persist(other);

        // mappings
        // BASIC: college 이름은 College.findBelongingCollege(major, entryYear).getName()와 동일해야 함
        String college = com.plzgraduate.myongjigraduatebe.user.domain.model.College
                .findBelongingCollege(major, entryYear).getName();
        em.persist(com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity
                .builder()
                .lectureJpaEntity(basic)
                .college(college)
                .build());

        // CORE: entryYear 사이
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(core1).startEntryYear(0).endEntryYear(99).build());
        em.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(core2).startEntryYear(0).endEntryYear(99).build());

        // MANDATORY_MAJOR / ELECTIVE_MAJOR
        em.persist(com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity.builder()
                .lectureJpaEntity(mand).major(major).mandatory(1).startEntryYear(0).endEntryYear(99).build());
        em.persist(com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity.builder()
                .lectureJpaEntity(elect).major(major).mandatory(0).startEntryYear(0).endEntryYear(99).build());

        // taken: 각 강의당 최소 1건(집계 대상에 포함되도록)
        persistTaken(basic);
        persistTaken(core1);
        persistTaken(core2);
        persistTaken(mand);
        persistTaken(elect);
        // taken for unmapped lecture → category null, contributes to filter(false)
        persistTaken(other);

        em.flush();
        em.clear();

        // when
        var sections = repository.getSections(major, entryYear);

        // then: COMMON은 없으므로 제외되고, 순서는 BASIC -> CORE -> MANDATORY -> ELECTIVE
        assertThat(sections).hasSize(4);
        assertThat(sections.get(0).getCategoryName()).isEqualTo(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE);
        assertThat(sections.get(0).getTotal()).isEqualTo(1);
        assertThat(sections.get(1).getCategoryName()).isEqualTo(PopularLectureCategory.CORE_CULTURE);
        assertThat(sections.get(1).getTotal()).isEqualTo(2);
        assertThat(sections.get(2).getCategoryName()).isEqualTo(PopularLectureCategory.MANDATORY_MAJOR);
        assertThat(sections.get(2).getTotal()).isEqualTo(1);
        assertThat(sections.get(3).getCategoryName()).isEqualTo(PopularLectureCategory.ELECTIVE_MAJOR);
        assertThat(sections.get(3).getTotal()).isEqualTo(1);
    }

    @Test
    @DisplayName("toDto null-guard 분기(credit/total/avg null) 경로를 반사로 커버")
    void toDto_nullGuardsCoveredViaReflection() throws Exception {
        // downcast to impl to call private method
        TakenLectureRepositoryImpl impl = (TakenLectureRepositoryImpl) repository;
        var tuple = Mockito.mock(com.querydsl.core.Tuple.class);
        @SuppressWarnings("unchecked")
        NumberExpression<Long> countExp = (NumberExpression<Long>) Mockito.mock(NumberExpression.class);

        // stub tuple getters used in toDto
        Mockito.when(tuple.get(QTakenLectureJpaEntity.takenLectureJpaEntity.lecture.id)).thenReturn("X");
        Mockito.when(tuple.get(QTakenLectureJpaEntity.takenLectureJpaEntity.lecture.name)).thenReturn("NameX");
        Mockito.when(tuple.get(QTakenLectureJpaEntity.takenLectureJpaEntity.lecture.credit)).thenReturn(null); // trigger credit null branch
        Mockito.when(tuple.get(countExp)).thenReturn(null); // trigger total null branch
        Mockito.when(tuple.get(4, Double.class)).thenReturn(null); // trigger avg null branch

        java.lang.reflect.Method m = TakenLectureRepositoryImpl.class.getDeclaredMethod(
                "toDto",
                com.querydsl.core.Tuple.class,
                com.querydsl.core.types.dsl.NumberExpression.class,
                PopularLectureCategory.class
        );
        m.setAccessible(true);
        PopularLectureDto dto = (PopularLectureDto) m.invoke(impl, tuple, countExp, PopularLectureCategory.CORE_CULTURE);

        assertThat(dto.getLectureId()).isEqualTo("X");
        assertThat(dto.getLectureName()).isEqualTo("NameX");
        assertThat(dto.getCredit()).isEqualTo(0); // credit null → 0
        assertThat(dto.getTotalCount()).isEqualTo(0L); // total null → 0
        assertThat(dto.getAverageRating()).isEqualTo(0.0); // avg null → 0.0
        assertThat(dto.getCategoryName()).isEqualTo(PopularLectureCategory.CORE_CULTURE);
    }
}
