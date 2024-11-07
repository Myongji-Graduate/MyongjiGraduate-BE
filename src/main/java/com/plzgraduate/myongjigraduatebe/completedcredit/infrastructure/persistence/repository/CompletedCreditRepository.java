package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository;

import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedCreditRepository extends JpaRepository<CompletedCreditJpaEntity, Long> {

	List<CompletedCreditJpaEntity> findAllByUserJpaEntity(UserJpaEntity userJpaEntity);

	void deleteAllByUserJpaEntity(UserJpaEntity userJpaEntity);
}
