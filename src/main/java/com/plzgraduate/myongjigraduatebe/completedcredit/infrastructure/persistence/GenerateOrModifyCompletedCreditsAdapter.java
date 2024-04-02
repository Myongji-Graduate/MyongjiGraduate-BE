package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.GenerateOrModifyCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper.CompletedCreditPersistenceMapper;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository.CompletedCreditRepository;
import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class GenerateOrModifyCompletedCreditsAdapter implements GenerateOrModifyCompletedCreditPort {

	private final CompletedCreditPersistenceMapper completedCreditPersistenceMapper;
	private final CompletedCreditRepository completedCreditRepository;

	@Override
	public void generateOrModifyCompletedCredits(List<CompletedCredit> completedCredits) {
		List<CompletedCreditJpaEntity> completedCreditJpaEntities = completedCredits.stream()
			.map(completedCreditPersistenceMapper::mapToJpaEntity)
			.collect(Collectors.toList());

		completedCreditRepository.saveAll(completedCreditJpaEntities);
	}
}
