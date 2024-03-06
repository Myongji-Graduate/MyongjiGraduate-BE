package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CommonCultureJpaEntity;

public interface CommonCultureRepository extends JpaRepository<CommonCultureJpaEntity, Long> {

	@Query("select cc from CommonCultureJpaEntity cc join fetch cc.lectureJpaEntity where cc.startEntryYear <= :entryYear and cc.endEntryYear >= :entryYear and cc.lectureJpaEntity.lectureCode not in ('KMA02123', 'KMA02124', 'KMA02125', 'KMA02126')")
	List<CommonCultureJpaEntity> findEng12GraduationCommonCulturesByEntryYear(@Param("entryYear") int entryYear);

	@Query("select cc from CommonCultureJpaEntity cc join fetch cc.lectureJpaEntity where cc.startEntryYear <= :entryYear and cc.endEntryYear >= :entryYear and cc.lectureJpaEntity.lectureCode not in ('KMA02106', 'KMA02107', 'KMA02108', 'KMA02109')")
	List<CommonCultureJpaEntity> findEng34GraduationCommonCulturesByEntryYear(@Param("entryYear") int entryYear);

	@Query("select cc from CommonCultureJpaEntity cc join fetch cc.lectureJpaEntity where cc.startEntryYear <= :entryYear and cc.endEntryYear >= :entryYear and cc.commonCultureCategory != 'ENGLISH'")
	List<CommonCultureJpaEntity> findEngFreeGraduationCommonCulturesByEntryYear(@Param("entryYear") int entryYear);
}
