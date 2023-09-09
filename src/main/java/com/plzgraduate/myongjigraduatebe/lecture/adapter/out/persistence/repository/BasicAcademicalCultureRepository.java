package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.BasicAcademicalCultureJpaEntity;

public interface BasicAcademicalCultureRepository extends JpaRepository<BasicAcademicalCultureJpaEntity, Long> {

	List<BasicAcademicalCultureJpaEntity> findAllByCollege(String college);
}
