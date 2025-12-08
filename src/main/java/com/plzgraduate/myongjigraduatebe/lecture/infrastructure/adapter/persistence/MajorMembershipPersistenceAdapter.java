package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.MajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureRepository;
import lombok.RequiredArgsConstructor;

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
}


