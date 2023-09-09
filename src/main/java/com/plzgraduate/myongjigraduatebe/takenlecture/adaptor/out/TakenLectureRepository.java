package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;

public interface TakenLectureRepository extends JpaRepository<TakenLectureJpaEntity, Long> {

	void deleteAllByUser(UserJpaEntity user);

	@Query("SELECT t from TakenLectureJpaEntity t join fetch t.lecture l WHERE t.user.id = :userId")
	List<TakenLectureJpaEntity> findTakenLectureJpaEntityWithLectureByUser(@Param("userId") Long userId);
}
