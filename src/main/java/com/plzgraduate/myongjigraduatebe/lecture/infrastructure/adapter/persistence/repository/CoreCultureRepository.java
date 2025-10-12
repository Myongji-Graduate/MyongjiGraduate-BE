package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoreCultureRepository extends JpaRepository<CoreCultureJpaEntity, Long> {

	@Query("select cc from CoreCultureJpaEntity cc join fetch cc.lectureJpaEntity where cc.startEntryYear <= :entryYear and cc.endEntryYear >= :entryYear")
	List<CoreCultureJpaEntity> findAllByEntryYear(@Param("entryYear") int entryYear);

    @Query("select c.lectureJpaEntity.id " +
            "from CoreCultureJpaEntity c " +
            "where c.lectureJpaEntity.id in :ids")
    List<String> findIdsByLectureIdIn(@Param("ids") List<String> ids);

    @Query("select c.lectureJpaEntity.id " +
            "from CoreCultureJpaEntity c " +
            "where c.lectureJpaEntity.id in :ids " +
            "and c.startEntryYear <= :entryYear " +
            "and c.endEntryYear >= :entryYear")
    List<String> findIdsByLectureIdInAndEntryYearBetween(
            @Param("ids") List<String> lectureIds,
            @Param("entryYear") int entryYear
    );
}
