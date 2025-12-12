package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.MajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PersistenceAdapter
@RequiredArgsConstructor
public class MajorMembershipPersistenceAdapter implements MajorMembershipPort {

    private final MajorLectureRepository majorLectureRepository;

    @Override
    public boolean isMajorLecture(String lectureId) {
        if (lectureId == null) {
            return false;
        }
        return majorLectureRepository.existsByLectureJpaEntity_Id(lectureId);
    }

    @Override
    public Set<String> findMajorLectureIds(List<String> lectureIds) {
        if (lectureIds == null || lectureIds.isEmpty()) {
            return Set.of();
        }
        // 전공필수와 전공선택 모두 포함
        Set<String> result = new HashSet<>();
        result.addAll(majorLectureRepository.findIdsByLectureIdInAndIsMandatory(lectureIds, 1));
        result.addAll(majorLectureRepository.findIdsByLectureIdInAndIsMandatory(lectureIds, 0));
        return result;
    }
}


