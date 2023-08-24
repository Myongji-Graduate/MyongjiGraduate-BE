package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.LoadUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PersistenceAdapter
@RequiredArgsConstructor
public class UserPersistenceAdaptor implements LoadUserPort, SaveUserPort {

	private final UserMapper userMapper;

	private final UserRepository userRepository;

	@Override
	public void saveUser(User user) {
		userRepository.save(userMapper.mapToJpaEntity(user));
	}

	@Override
	public User loadUserByUserId(String userId) {
		UserJpaEntity userJpaEntity = userRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException(""));
		return userMapper.mapToDomainEntity(userJpaEntity);
	}
}
