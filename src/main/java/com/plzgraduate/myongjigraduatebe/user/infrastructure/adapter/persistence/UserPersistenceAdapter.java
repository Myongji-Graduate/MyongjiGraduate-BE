package com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence;

import java.util.Optional;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.adapter.out.persistence.mapper.UserMapper;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import com.plzgraduate.myongjigraduatebe.user.application.port.CheckUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.DeleteUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@PersistenceAdapter
@Slf4j
@RequiredArgsConstructor
class UserPersistenceAdapter implements FindUserPort, SaveUserPort, CheckUserPort, UpdateUserPort, DeleteUserPort {

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

	@Override
	public void deleteUser(User user) {
		userRepository.delete(userMapper.mapToJpaEntity(user));
	}
}
