package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.GenerateOrModifyCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper.CompletedCreditPersistenceMapper;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository.CompletedCreditRepository;
import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper.UserMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class GenerateOrModifyCompletedCreditsAdapter implements
	GenerateOrModifyCompletedCreditPort {

	private final CompletedCreditPersistenceMapper completedCreditPersistenceMapper;
	private final CompletedCreditRepository completedCreditRepository;
	private final UserMapper userMapper;

	@Override
	public void generateOrModifyCompletedCredits(User user,
		List<CompletedCredit> completedCredits) {
		UserJpaEntity userJpaEntity = userMapper.mapToJpaEntity(user);
		completedCreditRepository.deleteAllByUserJpaEntity(userJpaEntity);
		completedCreditRepository.flush();

		List<CompletedCreditJpaEntity> completedCreditJpaEntities = completedCredits.stream()
			.map(completedCredit -> completedCreditPersistenceMapper.mapToNewJpaEntity(user,
				completedCredit))
			.collect(Collectors.toList());

		completedCreditRepository.saveAll(completedCreditJpaEntities);
	}
}
