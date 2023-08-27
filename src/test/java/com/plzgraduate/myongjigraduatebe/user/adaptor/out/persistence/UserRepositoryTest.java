package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

class UserRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private UserRepository userRepository;

	@DisplayName("아이디로 사용자를 조회한다.")
	@Test
	void 아이디_사용자_조회() {
		//given
		UserJpaEntity user1 = createUserEntity("mju1001", "1q2w3e4r!", "60181666");
		UserJpaEntity user2 = createUserEntity("mju1002", "1q2w3e4r!", "60181667");
		userRepository.saveAll(List.of(user1, user2));
		//when
		Optional<UserJpaEntity> user = userRepository.findByAuthId("mju1001");

		//then
		assertThat(user).isPresent();
		assertThat(user.get().getAuthId()).isEqualTo("mju1001");
	}

	private UserJpaEntity createUserEntity(String authId, String password, String studentNumber) {
		return UserJpaEntity
			.builder()
			.authId(authId)
			.password(password)
			.studentNumber(studentNumber)
			.build();
	}

}
