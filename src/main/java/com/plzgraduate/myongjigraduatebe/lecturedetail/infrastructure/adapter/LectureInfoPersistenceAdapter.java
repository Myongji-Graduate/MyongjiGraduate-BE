package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.port.FindLectureInfoPort;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureInfoJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper.LectureDetailMapper;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository.LectureInfoRepository;
import lombok.RequiredArgsConstructor;


@PersistenceAdapter
@RequiredArgsConstructor
public class LectureInfoPersistenceAdapter implements FindLectureInfoPort {

    private final LectureInfoRepository lectureInfoRepository;
    private final LectureDetailMapper lectureDetailMapper;

    @Override
    public LectureInfo findBySubjectAndProfessor(String subject, String professor) {
        LectureInfoJpaEntity lectureInfoJpaEntities = lectureInfoRepository.findBySubjectAndProfessor(subject, professor);
        return lectureDetailMapper.mapToLectureInfoModel(lectureInfoJpaEntities);
    }
}
