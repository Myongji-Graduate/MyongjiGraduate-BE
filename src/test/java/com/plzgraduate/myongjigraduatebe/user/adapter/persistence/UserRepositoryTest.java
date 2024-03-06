package com.plzgraduate.myongjigraduatebe.user.adapter.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;

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

	@DisplayName("학번을 통해 유저를 조회한다.")
	@Test
	void findByStudentNumber() {
		//given
		String studentNumber = "60181666";
		UserJpaEntity userJpaEntity = createUserEntity("mju1001", "1q2w3e4r!", studentNumber);
		userRepository.save(userJpaEntity);

		//when
		Optional<UserJpaEntity> user = userRepository.findByStudentNumber(studentNumber);

		//then
		assertThat(user).isPresent();
		assertThat(user.get().getStudentNumber()).isEqualTo(studentNumber);
	}

	@DisplayName("아이디가 이미 존재하는지 확인한다.")
	@Test
	void 중복_아이디_확인() {
		//given
		String authId = "mju1001";
		UserJpaEntity user = createUserEntity(authId, "1q2w3e4r!", "60181666");
		userRepository.save(user);

		//when
		boolean check= userRepository.existsByAuthId(authId);

		//then
		assertThat(check).isTrue();
	}

	@DisplayName("학번이 이미 존재하는지 확인한다.")
	@Test
	void 중복_학번_확인() {
		//given
		String studentNumber = "60181666";
		UserJpaEntity user = createUserEntity("mju1001", "1q2w3e4r!", studentNumber);
		userRepository.save(user);

		//when
		boolean check= userRepository.existsByStudentNumber(studentNumber);

		//then
		assertThat(check).isTrue();
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
