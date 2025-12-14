package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureInfoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LectureInfoRepository extends JpaRepository<LectureInfoJpaEntity, Long> {

    List<LectureInfoJpaEntity> findAllBySubject(String subject);
}
