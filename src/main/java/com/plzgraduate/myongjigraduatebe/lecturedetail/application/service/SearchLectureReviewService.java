package com.plzgraduate.myongjigraduatebe.lecturedetail.application.service;


import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.port.SearchLectureReviewPort;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase.SearchLectureReviewUseCase;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;


@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchLectureReviewService implements SearchLectureReviewUseCase {

    private final SearchLectureReviewPort searchLectureReviewPort;

    @Override
    public Slice<LectureReview> findLectureReviewBySubjectAndProfessor(String subject, String professor, Pageable pageable) {
        return searchLectureReviewPort.findBySubjectAndProfessor(subject, professor, pageable);
    }
}
