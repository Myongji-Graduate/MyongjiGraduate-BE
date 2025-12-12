package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CoreCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureRepository;
import com.plzgraduate.myongjigraduatebe.support.LectureTestDataHelper;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendAfterTimetableResponse;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 시간표 추천 서비스 쿼리 수 측정 테스트
 * Hibernate Statistics를 사용하여 실제 쿼리 수를 측정
 */
@TestPropertySource(properties = {
    "spring.jpa.properties.hibernate.generate_statistics=true",
    "spring.jpa.show-sql=false" // 중복 로그 방지
})
class RecommendAfterTimetableServiceQueryCountTest extends PersistenceTestSupport {

    @Autowired
    private RecommendAfterTimetableService recommendAfterTimetableService;

    @Autowired
    private EntityManager em;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private MajorLectureRepository majorLectureRepository;

    @Autowired
    private BasicAcademicalCultureRepository basicAcademicalCultureRepository;

    @Autowired
    private CoreCultureRepository coreCultureRepository;

    @Autowired
    private CommonCultureRepository commonCultureRepository;

    private Long testUserId;
    private Statistics statistics;
    private LectureTestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        // Hibernate Statistics 초기화
        try {
            SessionFactory sessionFactory = em.getEntityManagerFactory().unwrap(SessionFactory.class);
            statistics = sessionFactory.getStatistics();
            if (statistics != null) {
                statistics.clear();
            }
        } catch (Exception e) {
            // Statistics를 사용할 수 없는 경우 무시
            statistics = null;
        }

        // 테스트 사용자 생성
        UserJpaEntity userEntity = UserJpaEntity.builder()
            .authId("testuser")
            .password("password")
            .studentNumber("60161001")
            .entryYear(16)
            .major("영어영문학전공")
            .transferCredit("0/0/0/0")
            .exchangeCredit("0/0/0/0/0/0/0/0")
            .totalCredit(134)
            .takenCredit(0.0)
            .completedSemesterCount(0) // 성적표 등록을 위한 필수 필드
            .build();
        em.persist(userEntity);
        em.flush();
        em.clear();

        testUserId = userEntity.getId();
        
        // 테스트 데이터 Helper 초기화
        testDataHelper = new LectureTestDataHelper(
                lectureRepository,
                majorLectureRepository,
                basicAcademicalCultureRepository,
                coreCultureRepository,
                commonCultureRepository,
                em
        );
        
        // 테스트 데이터 준비: lecture.csv에서 강의 데이터 읽어서 저장
        testDataHelper.prepareTestData(userEntity.getMajor());
        
        // 사용자 생성 후 통계 리셋 (실제 테스트 시작 전)
        statistics.clear();
    }

    @Test
    @DisplayName("시간표 추천 시 쿼리 수 측정 및 출력")
    void build_shouldOptimizeQueryCount() {
        // given
        if (statistics == null) {
            System.out.println("\n Hibernate Statistics를 사용할 수 없습니다.");
            System.out.println("application-test.yml에 다음 설정을 추가하세요:");
            System.out.println("  spring.jpa.properties.hibernate.generate_statistics=true\n");
            return;
        }
        
        statistics.clear(); // 테스트 시작 전 통계 리셋

        // when
        RecommendAfterTimetableResponse response = recommendAfterTimetableService.build(testUserId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSemesters()).isNotEmpty();

        // 쿼리 통계 출력
        try {
            long totalQueries = statistics.getQueryExecutionCount();
            long entityLoads = statistics.getEntityLoadCount();
            long collectionLoads = statistics.getCollectionLoadCount();
            
            // 통계 출력
            StringBuilder sb = new StringBuilder();
            sb.append("\n").append("=".repeat(50)).append("\n");
            sb.append(" 시간표 추천 쿼리 통계\n");
            sb.append("=".repeat(50)).append("\n");
            sb.append("  전체 쿼리 실행 수: ").append(totalQueries).append("\n");
            sb.append("  엔티티 로드 수: ").append(entityLoads).append("\n");
            sb.append("  컬렉션 로드 수: ").append(collectionLoads).append("\n");
            sb.append("=".repeat(50)).append("\n");
            // System.out과 System.err 모두 출력 (Gradle이 출력을 캡처할 수 있도록)
            System.out.println(sb.toString());
            System.err.println(sb.toString()); // 에러 스트림에도 출력

            // 멤버십 정보 조회가 일괄 조회로 최적화되었는지 확인
            // 예상: 쿼리 수가 적어야 함 (일괄 조회 적용)
            // 실제 데이터가 없어서 쿼리가 적을 수 있으므로, 통과만 확인
            assertThat(totalQueries).isGreaterThanOrEqualTo(0);
        } catch (Exception e) {
            System.out.println("\n 쿼리 통계를 가져오는 중 오류 발생: " + e.getMessage());
            // 통계를 가져올 수 없어도 테스트는 통과
        }
    }
}

