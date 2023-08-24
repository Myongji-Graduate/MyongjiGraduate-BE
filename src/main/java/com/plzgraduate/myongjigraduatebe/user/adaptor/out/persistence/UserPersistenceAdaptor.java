package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.LoadUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

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
		UserJpaEntity userJpaEntity = userRepository.findByAuthId(userId)
			.orElseThrow(() -> new IllegalArgumentException(""));
		return userMapper.mapToDomainEntity(userJpaEntity);
	}
}
