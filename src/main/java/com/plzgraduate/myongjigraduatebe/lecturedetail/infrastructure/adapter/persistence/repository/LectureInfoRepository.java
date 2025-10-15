package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureInfoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface LectureInfoRepository extends JpaRepository<LectureInfoJpaEntity, Long> {

    LectureInfoJpaEntity findBySubjectAndProfessor(
            String subject, String professor
    );
}
