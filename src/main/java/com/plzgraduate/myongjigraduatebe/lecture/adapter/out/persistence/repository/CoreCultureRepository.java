package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CoreCultureJpaEntity;

public interface CoreCultureRepository extends JpaRepository<CoreCultureJpaEntity, Long> {

	@Query("select cc from CoreCultureJpaEntity cc where cc.startEntryYear <= :entryYear and cc.endEntryYear >= :entryYear")
	List<CoreCultureJpaEntity> findAllByEntryYear(@Param("entryYear") int entryYear);
}
