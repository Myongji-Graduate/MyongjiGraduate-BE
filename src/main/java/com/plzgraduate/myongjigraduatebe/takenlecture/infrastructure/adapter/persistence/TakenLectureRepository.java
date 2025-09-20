package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TakenLectureRepository extends JpaRepository<TakenLectureJpaEntity, Long> , TakenLectureRepositoryCustom{

	@Query("delete from TakenLectureJpaEntity t where t.user = :user")
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteAllByUser(@Param("user") UserJpaEntity user);

	@Query("SELECT t from TakenLectureJpaEntity t join fetch t.lecture join fetch t.user WHERE t.user = :user")
	List<TakenLectureJpaEntity> findTakenLectureJpaEntityWithLectureByUser(
		@Param("user") UserJpaEntity user);

	/**
	 * 유저가 이수한 과목 중, 주어진 코드(id) 집합에 해당하는 과목 id만 조회
	 * - LectureJpaEntity.id 가 곧 "강의코드(문자열)"
	 */
	@Query("select t.lecture.id " +
			"from TakenLectureJpaEntity t " +
			"where t.user.id = :userId " +
			"and t.lecture.id in :codes")
	List<String> findTakenLectureIdsByUserAndCodes(
			@Param("userId") Long userId,
			@Param("codes") Collection<String> codes);
}
