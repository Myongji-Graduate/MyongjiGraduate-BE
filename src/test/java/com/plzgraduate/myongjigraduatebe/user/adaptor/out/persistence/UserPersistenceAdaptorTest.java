package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.core.config.JpaAuditingConfig;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Import({UserPersistenceAdaptor.class, UserMapper.class})
class UserPersistenceAdaptorTest extends PersistenceTestSupport {

	@Autowired
	private UserPersistenceAdaptor userPersistenceAdaptor;
	@Autowired
	private UserRepository userRepository;

	@DisplayName("사용자를 저장한다.")
	@Test
	void 사용자_저장() {
	    //given
		User user = createUser("mju1001", "1q2w3e4r!", "60181666");
	    //when
		userPersistenceAdaptor.saveUser(user);
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
		User user = userPersistenceAdaptor.loadUserByAuthId(authId);

		//then
		assertThat(user.getAuthId()).isEqualTo(authId);

	}

	private User createUser(String authId, String password, String studentNumber) {
		return User
			.builder()
			.authId(authId)
			.password(password)
			.studentNumber(studentNumber)
			.build();
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
