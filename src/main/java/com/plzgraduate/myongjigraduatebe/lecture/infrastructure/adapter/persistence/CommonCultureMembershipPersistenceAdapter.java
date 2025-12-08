package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.CommonCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class CommonCultureMembershipPersistenceAdapter implements CommonCultureMembershipPort {

    private final CommonCultureRepository commonCultureRepository;

    @Override
    public boolean isCommonLecture(String lectureId) {
        if (lectureId == null) {
            return false;
        }
        return commonCultureRepository.existsByLectureJpaEntity_Id(lectureId);
    }
}


