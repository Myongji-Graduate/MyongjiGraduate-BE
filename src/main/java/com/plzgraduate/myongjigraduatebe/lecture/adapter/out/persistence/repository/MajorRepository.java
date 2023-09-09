package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.MajorJpaEntity;

public interface MajorRepository extends JpaRepository<MajorJpaEntity, Long> {

	List<MajorJpaEntity> findAllByMajor(String major);
}
