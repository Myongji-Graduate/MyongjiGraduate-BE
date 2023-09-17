package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CommonCultureJpaEntity;

public interface CommonCultureRepository extends JpaRepository<CommonCultureJpaEntity, Long> {

	@Query("select cc from CommonCultureJpaEntity cc join fetch cc.lectureJpaEntity where cc.startEntryYear <= :entryYear and cc.endEntryYear >= :entryYear")
	List<CommonCultureJpaEntity> findAllByEntryYear(@Param("entryYear") int entryYear);
}
