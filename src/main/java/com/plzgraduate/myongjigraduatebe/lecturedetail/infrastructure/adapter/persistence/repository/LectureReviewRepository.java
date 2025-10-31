package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LectureReviewRepository extends JpaRepository<LectureReviewJpaEntity, Long> {

    Slice<LectureReviewJpaEntity> findBySubjectAndProfessor(String subject, String professor, Pageable pageable);

    List<LectureReviewJpaEntity> findTop5BySubjectAndProfessorOrderByIdDesc(String subject, String professor);
}
