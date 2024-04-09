package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper.CompletedCreditPersistenceMapper;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository.CompletedCreditRepository;
import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class FindCompletedCreditAdapter implements FindCompletedCreditPort {

	private final UserMapper userMapper;
	private final CompletedCreditPersistenceMapper completedCreditPersistenceMapper;
	private final CompletedCreditRepository completedCreditRepository;

	@Override
	public List<CompletedCredit> findCompletedCredits(User user) {
		return completedCreditRepository.findAllByUserJpaEntity(userMapper.mapToJpaEntity(user)).stream()
			.map(completedCreditPersistenceMapper::mapToDomainModel)
			.collect(Collectors.toList());
	}

	@Override
	public CompletedCredit findCategorizedCompletedCredit(User user, GraduationCategory graduationCategory) {
		CompletedCreditJpaEntity completedCreditJpaEntity = completedCreditRepository.findByUserJpaEntityAndGraduationCategory(
			userMapper.mapToJpaEntity(user), graduationCategory)
			.orElseThrow(() -> new IllegalArgumentException("이수 구분에 해당하지 않는 졸업 분류입니다."));
		return completedCreditPersistenceMapper.mapToDomainModel(completedCreditJpaEntity);
	}

}
