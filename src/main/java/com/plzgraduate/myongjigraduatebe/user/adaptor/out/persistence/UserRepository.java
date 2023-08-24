package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
	Optional<UserJpaEntity> findByAuthId(String authId);
}
