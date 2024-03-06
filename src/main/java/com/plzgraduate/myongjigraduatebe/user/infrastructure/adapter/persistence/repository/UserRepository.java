package com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
	Optional<UserJpaEntity> findByAuthId(String authId);

	Optional<UserJpaEntity> findByStudentNumber(String studentNumber);

	boolean existsByAuthId(String authId);

	boolean existsByStudentNumber(String studentNumber);
}
