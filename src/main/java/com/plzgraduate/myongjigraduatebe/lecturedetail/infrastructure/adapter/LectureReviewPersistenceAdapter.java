package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.port.SearchLectureReviewPort;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper.LectureDetailMapper;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository.LectureReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


@PersistenceAdapter
@RequiredArgsConstructor
public class LectureReviewPersistenceAdapter implements SearchLectureReviewPort {

    private final LectureReviewRepository lectureReviewRepository;
    private final LectureDetailMapper lectureDetailMapper;

    @Override
    public Slice<LectureReview> findBySubjectAndProfessor(String subject, String professor, Pageable pageable) {
        Slice<LectureReviewJpaEntity> lectureReviewJpaEntities = lectureReviewRepository.findBySubjectAndProfessor(subject, professor, pageable);
        return lectureReviewJpaEntities.map(lectureDetailMapper::mapToLectureReviewModel);
    }
}
