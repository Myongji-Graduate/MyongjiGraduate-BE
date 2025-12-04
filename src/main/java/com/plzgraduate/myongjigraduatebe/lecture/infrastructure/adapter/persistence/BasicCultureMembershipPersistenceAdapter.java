package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.BasicCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.BasicAcademicalCultureRepository;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class BasicCultureMembershipPersistenceAdapter implements BasicCultureMembershipPort {

    private final BasicAcademicalCultureRepository basicAcademicalCultureRepository;

    @Override
    public boolean isBasicLecture(String lectureId) {
        if (lectureId == null) {
            return false;
        }
        return basicAcademicalCultureRepository.existsByLectureJpaEntity_Id(lectureId);
    }
}


