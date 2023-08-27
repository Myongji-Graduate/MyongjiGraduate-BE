package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.CheckUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.LoadUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@PersistenceAdapter
@Slf4j
@RequiredArgsConstructor
class UserPersistenceAdaptor implements LoadUserPort, SaveUserPort, CheckUserPort {

	private final UserMapper userMapper;

	private final UserRepository userRepository;

	@Override
	public void saveUser(User user) {
		userRepository.save(userMapper.mapToJpaEntity(user));
	}

	@Override
	public User loadUserByAuthId(String authId) {
		UserJpaEntity userJpaEntity = userRepository.findByAuthId(authId)
			.orElseThrow(() -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다."));
		return userMapper.mapToDomainEntity(userJpaEntity);
	}

	@Override
	public boolean checkDuplicateAuthId(String authId) {
		return userRepository.existsByAuthId(authId);
	}

	@Override
	public boolean checkDuplicateStudentNumber(String studentNumber) {
		return userRepository.existsByStudentNumber(studentNumber);
	}
}
