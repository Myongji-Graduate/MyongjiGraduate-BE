package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.GetPopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.QFindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CoreCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureRepository;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory.*;


@Repository
@RequiredArgsConstructor
public class TakenLectureQueryRepositoryImpl
        implements TakenLectureQueryRepositoryCustom, GetPopularLecturePort {

    private static final QTakenLectureJpaEntity takenLecture = QTakenLectureJpaEntity.takenLectureJpaEntity;

    private final JPAQueryFactory jpaQueryFactory;

    private final MajorLectureRepository majorRepository;
    private final CoreCultureRepository coreCultureRepository;
    private final BasicAcademicalCultureRepository basicAcademicalCultureRepository;
    private final CommonCultureRepository commonCultureRepository;

    @Override
    public List<FindPopularLectureDto> getPopularLecturesByTotalCount() {
        // 1) 인기 강의 집계 쿼리
        List<FindPopularLectureDto> raw = jpaQueryFactory
                .select(new QFindPopularLectureDto(
                        takenLecture.lecture.id,
                        takenLecture.lecture.name,
                        takenLecture.lecture.credit,
                        takenLecture.id.count()
                ))
                .from(takenLecture)
                .groupBy(
                        takenLecture.lecture.id,
                        takenLecture.lecture.name,
                        takenLecture.lecture.credit
                )
                .orderBy(takenLecture.id.count().desc(), takenLecture.lecture.id.desc())
                .fetch();

        // 2) lectureId 수집
        List<String> lectureIds = raw.stream()
                .map(FindPopularLectureDto::getLectureId)
                .toList();

        // 3) 카테고리별 일괄 조회 (IN 쿼리)
        Set<String> majorMandatoryIds = new HashSet<>(majorRepository.findIdsByLectureIdInAndIsMandatory(lectureIds, 1));
        Set<String> majorOptionalIds = new HashSet<>(majorRepository.findIdsByLectureIdInAndIsMandatory(lectureIds, 0));
        Set<String> coreCultureIds = new HashSet<>(coreCultureRepository.findIdsByLectureIdIn(lectureIds));
        Set<String> basicCultureIds = new HashSet<>(basicAcademicalCultureRepository.findIdsByLectureIdIn(lectureIds));
        Set<String> commonCultureIds = new HashSet<>(commonCultureRepository.findIdsByLectureIdIn(lectureIds));

        // 4) 후처리: categoryName 매핑
        return raw.stream()
                .map(dto -> FindPopularLectureDto.of(
                        dto.getLectureId(),
                        dto.getLectureName(),
                        dto.getCredit(),
                        dto.getTotalCount(),
                        resolveCategoryName(
                                dto.getLectureId(),
                                majorMandatoryIds,
                                majorOptionalIds,
                                coreCultureIds,
                                basicCultureIds,
                                commonCultureIds))
                )
                .toList();
    }

    private PopularLectureCategory resolveCategoryName(
            String lectureId,
            Set<String> majorMandatoryIds,
            Set<String> majorOptionalIds,
            Set<String> coreCultureIds,
            Set<String> basicCultureIds,
            Set<String> commonCultureIds
    ) {
        if (majorMandatoryIds.contains(lectureId)) return MANDATORY_MAJOR;
        if (majorOptionalIds.contains(lectureId)) return ELECTIVE_MAJOR;
        if (coreCultureIds.contains(lectureId)) return CORE_CULTURE;
        if (basicCultureIds.contains(lectureId)) return BASIC_ACADEMICAL_CULTURE;
        if (commonCultureIds.contains(lectureId)) return COMMON_CULTURE;
        return NORMAL_CULTURE;
    }
}

