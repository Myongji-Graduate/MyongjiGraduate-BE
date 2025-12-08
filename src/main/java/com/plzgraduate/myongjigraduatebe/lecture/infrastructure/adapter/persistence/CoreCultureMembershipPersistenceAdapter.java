package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.CoreCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CoreCultureRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public Set<String> findCoreLectureIds(List<String> lectureIds) {
        if (lectureIds == null || lectureIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(coreCultureRepository.findIdsByLectureIdIn(lectureIds));
    }
}


