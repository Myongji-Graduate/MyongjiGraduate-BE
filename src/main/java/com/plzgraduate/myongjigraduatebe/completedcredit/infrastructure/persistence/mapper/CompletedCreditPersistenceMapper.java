package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;

@Component
public class CompletedCreditPersistenceMapper {

	public CompletedCredit mapToDomainModel(CompletedCreditJpaEntity completedCreditJpaEntity) {
		return CompletedCredit.builder()
			.category(completedCreditJpaEntity.getCategory())
			.totalCredit(completedCreditJpaEntity.getTotalCredit())
			.takenCredit(completedCreditJpaEntity.getTakenCredit()).build();
	}



}
