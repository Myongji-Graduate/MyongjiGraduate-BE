package com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserJpaEntity u where u.id = :id")
    Optional<UserJpaEntity> findByIdForUpdate(@Param("id") Long id);

	Optional<UserJpaEntity> findByAuthId(String authId);

	Optional<UserJpaEntity> findByStudentNumber(String studentNumber);

	boolean existsByAuthId(String authId);

	boolean existsByStudentNumber(String studentNumber);
}
