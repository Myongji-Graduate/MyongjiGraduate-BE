package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;

public interface TakenLectureRepository extends JpaRepository<TakenLectureJpaEntity, Long> {

	@Modifying
	@Query("DELETE FROM TakenLectureJpaEntity t WHERE t.user = :user")
	void deleteAllByUser(@Param("user") UserJpaEntity user);
}
