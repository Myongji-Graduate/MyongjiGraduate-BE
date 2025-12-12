package com.plzgraduate.myongjigraduatebe.support;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CoreCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 시간표 추천 쿼리 테스트를 위한 테스트 데이터 준비 Helper 클래스
 */
public class LectureTestDataHelper {

    private final LectureRepository lectureRepository;
    private final MajorLectureRepository majorLectureRepository;
    private final BasicAcademicalCultureRepository basicAcademicalCultureRepository;
    private final CoreCultureRepository coreCultureRepository;
    private final CommonCultureRepository commonCultureRepository;
    private final EntityManager entityManager;

    public LectureTestDataHelper(
            LectureRepository lectureRepository,
            MajorLectureRepository majorLectureRepository,
            BasicAcademicalCultureRepository basicAcademicalCultureRepository,
            CoreCultureRepository coreCultureRepository,
            CommonCultureRepository commonCultureRepository,
            EntityManager entityManager
    ) {
        this.lectureRepository = lectureRepository;
        this.majorLectureRepository = majorLectureRepository;
        this.basicAcademicalCultureRepository = basicAcademicalCultureRepository;
        this.coreCultureRepository = coreCultureRepository;
        this.commonCultureRepository = commonCultureRepository;
        this.entityManager = entityManager;
    }

    /**
     * 테스트용 강의 데이터를 생성하여 DB에 저장하고, 멤버십 정보를 추가합니다.
     * 쿼리 수 측정을 위해 충분한 양의 강의 데이터를 생성합니다.
     *
     * @param major 사용자의 전공
     * @param entryYear 사용자의 입학년도
     */
    public void prepareTestData(String major, int entryYear) {
        // 테스트용 강의 데이터 생성 (쿼리 수 측정을 위해 충분한 양 생성)
        List<LectureJpaEntity> lectures = createTestLectures(100);

        // 강의 데이터 저장
        lectureRepository.saveAll(lectures);
        entityManager.flush();
        entityManager.clear();

        // 멤버십 정보 추가
        addMajorMemberships(lectures, major, entryYear);
        addBasicCultureMemberships(lectures);
        addCoreCultureMemberships(lectures, entryYear);
        addCommonCultureMemberships(lectures, entryYear);

        entityManager.flush();
        entityManager.clear();
    }

    /**
     * 테스트용 강의 데이터를 생성합니다.
     *
     * @param count 생성할 강의 개수
     * @return 생성된 강의 리스트
     */
    private List<LectureJpaEntity> createTestLectures(int count) {
        List<LectureJpaEntity> lectures = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String lectureCode = String.format("TEST%03d", i);
            String name = String.format("테스트강의%d", i);
            lectures.add(LectureJpaEntity.builder()
                    .id(lectureCode)
                    .name(name)
                    .credit(3)
                    .isRevoked(0)
                    .duplicateCode(null)
                    .build());
        }
        return lectures;
    }

    private void addMajorMemberships(List<LectureJpaEntity> lectures, String major, int entryYear) {
        List<MajorLectureJpaEntity> majorLectures = new ArrayList<>();
        int count = Math.min(10, lectures.size());
        
        for (int i = 0; i < count; i++) {
            LectureJpaEntity lecture = lectures.get(i);
            // 전공필수
            majorLectures.add(MajorLectureJpaEntity.builder()
                    .lectureJpaEntity(lecture)
                    .major(major)
                    .mandatory(1)
                    .startEntryYear(16)
                    .endEntryYear(99)
                    .build());
            // 전공선택
            majorLectures.add(MajorLectureJpaEntity.builder()
                    .lectureJpaEntity(lecture)
                    .major(major)
                    .mandatory(0)
                    .startEntryYear(16)
                    .endEntryYear(99)
                    .build());
        }
        majorLectureRepository.saveAll(majorLectures);
    }

    private void addBasicCultureMemberships(List<LectureJpaEntity> lectures) {
        List<BasicAcademicalCultureLectureJpaEntity> basicCultures = new ArrayList<>();
        int startIndex = 10;
        int endIndex = Math.min(20, lectures.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            LectureJpaEntity lecture = lectures.get(i);
            basicCultures.add(BasicAcademicalCultureLectureJpaEntity.builder()
                    .lectureJpaEntity(lecture)
                    .college(College.HUMANITIES.getName())
                    .build());
        }
        basicAcademicalCultureRepository.saveAll(basicCultures);
    }

    private void addCoreCultureMemberships(List<LectureJpaEntity> lectures, int entryYear) {
        List<CoreCultureJpaEntity> coreCultures = new ArrayList<>();
        CoreCultureCategory[] categories = CoreCultureCategory.values();
        int startIndex = 20;
        int endIndex = Math.min(30, lectures.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            LectureJpaEntity lecture = lectures.get(i);
            coreCultures.add(CoreCultureJpaEntity.builder()
                    .lectureJpaEntity(lecture)
                    .coreCultureCategory(categories[i % categories.length])
                    .startEntryYear(16)
                    .endEntryYear(99)
                    .build());
        }
        coreCultureRepository.saveAll(coreCultures);
    }

    private void addCommonCultureMemberships(List<LectureJpaEntity> lectures, int entryYear) {
        List<CommonCultureJpaEntity> commonCultures = new ArrayList<>();
        int startIndex = 30;
        int endIndex = Math.min(40, lectures.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            LectureJpaEntity lecture = lectures.get(i);
            commonCultures.add(CommonCultureJpaEntity.builder()
                    .lectureJpaEntity(lecture)
                    .commonCultureCategory(CommonCultureCategory.ENGLISH)
                    .startEntryYear(16)
                    .endEntryYear(99)
                    .build());
        }
        commonCultureRepository.saveAll(commonCultures);
    }
}

