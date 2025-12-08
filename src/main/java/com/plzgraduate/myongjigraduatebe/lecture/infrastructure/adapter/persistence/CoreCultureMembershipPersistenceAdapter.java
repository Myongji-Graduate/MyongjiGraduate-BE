package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.CoreCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CoreCultureRepository;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class CoreCultureMembershipPersistenceAdapter implements CoreCultureMembershipPort {

    private final CoreCultureRepository coreCultureRepository;

    @Override
    public boolean isCoreLecture(String lectureId) {
        if (lectureId == null) {
            return false;
        }
        return coreCultureRepository.existsByLectureJpaEntity_Id(lectureId);
    }
}


