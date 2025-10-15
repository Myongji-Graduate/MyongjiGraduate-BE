package com.plzgraduate.myongjigraduatebe.lecturedetail.application.port;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface SearchLectureReviewPort {

    Slice<LectureReview> findBySubjectAndProfessor(String subject, String professor, Pageable pageable);
}
