package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.port.FindLectureInfoPort;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureInfoJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper.LectureDetailMapper;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository.LectureInfoRepository;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository.LectureReviewRepository;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@PersistenceAdapter
@RequiredArgsConstructor
public class LectureInfoPersistenceAdapter implements FindLectureInfoPort {

    private final LectureInfoRepository lectureInfoRepository;
    private final LectureReviewRepository lectureReviewRepository;
    private final LectureDetailMapper lectureDetailMapper;

    @Override
    public List<LectureInfo> findBySubject(String subject) {
        List<LectureInfoJpaEntity> lectureInfoJpaEntities = lectureInfoRepository.findAllBySubject(subject);

        Map<String, List<LectureReviewJpaEntity>> lectureReviewJpaEntities =
                lectureInfoJpaEntities.stream()
                        .collect(Collectors.toMap(
                                LectureInfoJpaEntity::getProfessor,
                                info -> lectureReviewRepository
                                        .findTop5BySubjectAndProfessorOrderByIdDesc(
                                                info.getSubject(), info.getProfessor()),
                                (a, b) -> a,
                                LinkedHashMap::new
                        ));

        return lectureDetailMapper.mapToLectureInfoModels(lectureInfoJpaEntities, lectureReviewJpaEntities);
    }
}
