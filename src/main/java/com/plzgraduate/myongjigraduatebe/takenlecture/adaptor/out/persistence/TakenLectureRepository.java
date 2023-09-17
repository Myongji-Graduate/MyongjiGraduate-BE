package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;

public interface TakenLectureRepository extends JpaRepository<TakenLectureJpaEntity, Long> {

	@Query("delete from TakenLectureJpaEntity t where t.user = :user")
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteAllByUser(@Param("user") UserJpaEntity user);

	@Query("SELECT t from TakenLectureJpaEntity t join fetch t.lecture join fetch t.user WHERE t.user = :user")
	List<TakenLectureJpaEntity> findTakenLectureJpaEntityWithLectureByUser(@Param("user") UserJpaEntity user);
}
