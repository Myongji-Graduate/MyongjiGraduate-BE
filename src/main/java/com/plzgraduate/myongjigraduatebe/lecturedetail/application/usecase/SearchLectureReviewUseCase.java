package com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface SearchLectureReviewUseCase {

    Slice<LectureReview> findLectureReviewBySubjectAndProfessor(String subject, String professor, Pageable pageable);
}
