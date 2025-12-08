package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.CommonCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public Set<String> findCommonLectureIds(List<String> lectureIds) {
        if (lectureIds == null || lectureIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(commonCultureRepository.findIdsByLectureIdIn(lectureIds));
    }
}


