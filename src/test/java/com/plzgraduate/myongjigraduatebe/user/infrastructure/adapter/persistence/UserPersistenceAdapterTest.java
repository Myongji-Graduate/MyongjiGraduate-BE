package com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserPersistenceAdapterTest extends PersistenceTestSupport {

	@Autowired
	private UserPersistenceAdapter userPersistenceAdapter;
	@Autowired
	private UserRepository userRepository;

	@DisplayName("사용자를 저장한다.")
	@Test
	void 사용자_저장() {
		//given
		User user = createUser("mju1001", "1q2w3e4r!", "60181666");
		//when
		userPersistenceAdapter.saveUser(user);
		//then
		assertThat(userRepository.findByAuthId("mju1001")).isPresent();
	}

	@DisplayName("아이디로 사용자를 조회한다.")
	@Test
	void 아아디_사용자_조회() {
		//given
		String authId = "mju1001";
		UserJpaEntity userEntity = createUserEntity(authId, "1q2w3e4r!", "60181666");
		userRepository.save(userEntity);
		//when
		Optional<User> user = userPersistenceAdapter.findUserByAuthId(authId);

		//then
		assertThat(user).isPresent();
		assertThat(user.get()
			.getAuthId()).isEqualTo(authId);
	}

	@DisplayName("학번으로 사용자를 조회한다.")
	@Test
	void findUserByStudentNumber() {
		//given
		String studentNumber = "60181666";
		UserJpaEntity userJpaEntity = createUserEntity("mju1001", "1q2w3e4r!", studentNumber);
		userRepository.save(userJpaEntity);

		//when
		Optional<User> user = userPersistenceAdapter.findUserByStudentNumber(studentNumber);

		//then
		assertThat(user).isPresent();
		assertThat(user.get()
			.getStudentNumber()).isEqualTo(studentNumber);
	}

	@DisplayName("아이디가 이미 존재하는지 확인한다.")
	@Test
	void 중복_아이디_확인() {
		//given
		String authId = "mju1001";
		UserJpaEntity user = createUserEntity(authId, "1q2w3e4r!", "60181666");
		userRepository.save(user);
		//when
		boolean check = userPersistenceAdapter.checkDuplicateAuthId(authId);

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
		boolean check = userPersistenceAdapter.checkDuplicateStudentNumber(studentNumber);

		//then
		assertThat(check).isTrue();
	}

	@DisplayName("유저 데이터를 삭제한다.")
	@Test
	void deleteUser() {
		//given
		String authId = "mju1000";
		UserJpaEntity userJpaEntity = createUserEntity(authId, "1q2w3e4r!", "60181666");
		UserJpaEntity savedUserJpaEntity = userRepository.save(userJpaEntity);
		User user = User.builder()
			.id(savedUserJpaEntity.getId())
			.build();

		//when
		userPersistenceAdapter.deleteUser(user);

		//then
		Optional<User> foundUser = userPersistenceAdapter.findUserByAuthId(authId);
		assertThat(foundUser.isPresent()).isFalse();
	}

	private User createUser(String authId, String password, String studentNumber) {
		return User
				.builder()
				.authId(authId)
				.password(password)
				.studentNumber(studentNumber)
				.transferCredit(new TransferCredit(0, 0, 0, 0))
				.studentCategory(StudentCategory.NORMAL)
				.build();
	}


	private UserJpaEntity createUserEntity(String authId, String password, String studentNumber) {
		return UserJpaEntity
				.builder()
				.authId(authId)
				.password(password)
				.studentNumber(studentNumber)
				.transferCredit("0/0/0/0")
				.studentCategory(StudentCategory.NORMAL)
				.build();
	}

}
