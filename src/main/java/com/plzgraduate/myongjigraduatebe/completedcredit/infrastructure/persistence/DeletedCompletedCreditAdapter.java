package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.DeleteCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository.CompletedCreditRepository;
import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@Transactional
@RequiredArgsConstructor
public class DeletedCompletedCreditAdapter implements DeleteCompletedCreditPort {

	private final CompletedCreditRepository completedCreditRepository;
	private final UserMapper userMapper;

	@Override
	public void deleteAllCompletedCredits(User user) {
		completedCreditRepository.deleteAllByUserJpaEntity(userMapper.mapToJpaEntity(user));
	}
}
