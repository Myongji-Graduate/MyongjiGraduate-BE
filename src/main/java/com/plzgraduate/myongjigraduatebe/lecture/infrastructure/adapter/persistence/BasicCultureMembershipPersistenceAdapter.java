package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.BasicCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.BasicAcademicalCultureRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public Set<String> findBasicLectureIds(List<String> lectureIds) {
        if (lectureIds == null || lectureIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(basicAcademicalCultureRepository.findIdsByLectureIdIn(lectureIds));
    }
}


