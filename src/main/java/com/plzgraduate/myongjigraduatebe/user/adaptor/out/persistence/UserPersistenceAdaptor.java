package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import java.util.Optional;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.CheckUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@PersistenceAdapter
@Slf4j
@RequiredArgsConstructor
class UserPersistenceAdaptor implements FindUserPort, SaveUserPort, CheckUserPort, UpdateUserPort {

	private final UserMapper userMapper;

	private final UserRepository userRepository;

	@Override
	public void saveUser(User user) {
		userRepository.save(userMapper.mapToJpaEntity(user));
	}

	@Override
	public Optional<User> findUserById(Long id) {
		UserJpaEntity userJpaEntity = userRepository.findById(id).orElse(null);
		if(userJpaEntity == null) {
			return Optional.empty();
		}
		return Optional.of(userMapper.mapToDomainEntity(userJpaEntity));
	}

	@Override
	public Optional<User> findUserByAuthId(String authId) {
		UserJpaEntity userJpaEntity = userRepository.findByAuthId(authId).orElse(null);
		if(userJpaEntity == null) {
			return Optional.empty();
		}
		return Optional.of(userMapper.mapToDomainEntity(userJpaEntity));
	}

	@Override
	public Optional<User> findUserByStudentNumber(String studentNumber) {
		return userRepository.findByStudentNumber(studentNumber).map(userMapper::mapToDomainEntity);
	}

	@Override
	public boolean checkDuplicateAuthId(String authId) {
		return userRepository.existsByAuthId(authId);
	}

	@Override
	public boolean checkDuplicateStudentNumber(String studentNumber) {
		return userRepository.existsByStudentNumber(studentNumber);
	}

	@Override
	public void updateUser(User user) {
		userRepository.save(userMapper.mapToJpaEntity(user));
	}
}
