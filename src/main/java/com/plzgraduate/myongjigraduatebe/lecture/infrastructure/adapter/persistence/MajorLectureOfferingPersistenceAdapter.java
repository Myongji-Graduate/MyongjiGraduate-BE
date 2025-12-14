package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;


import com.plzgraduate.myongjigraduatebe.lecture.application.port.MajorLectureOfferingPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLectureOffering;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureOfferingJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.MajorLectureOfferingMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureOfferingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MajorLectureOfferingPersistenceAdapter implements MajorLectureOfferingPort {

    private final MajorLectureOfferingJpaRepository jpaRepository;
    private final MajorLectureOfferingMapper mapper;

    @Override
    public Optional<MajorLectureOffering> findByLectureId(String lectureId) {
        return jpaRepository.findById(lectureId)
                .map(mapper::toDomain);
    }

    @Override
    public MajorLectureOffering save(MajorLectureOffering offering) {
        MajorLectureOfferingJpaEntity entity = mapper.toEntity(offering);
        MajorLectureOfferingJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
