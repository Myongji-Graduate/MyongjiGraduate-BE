package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.user.application.port.out.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class FindUserServiceTest {
	@Mock
	private FindUserPort findUserPort;

	@InjectMocks
	private FindUserService findUserService;

	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.authId("tester00")
			.password("tester00!")
			.studentNumber("60191656")
			.build();
	}

	@DisplayName("id를 통해 User를 찾는다.")
	@Test
	void findUserByExistId() {
		//given
		given(findUserPort.findUserById(1L)).willReturn(Optional.of(user));

		//when
		User userById = findUserService.findUserById(1L);

		//test
		assertThat(userById)
			.extracting("id", "authId", "password")
			.contains(1L, "tester00", "tester00!");
	}

	@DisplayName("존재하지 않는 id일 경우 예외가 발생한다.")
	@Test
	void findUserByNotExistId() {
		//given
		given(findUserPort.findUserById(1L)).willReturn(Optional.empty());

		//when //test
		assertThatThrownBy(() -> findUserService.findUserById(1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 사용자를 찾을 수 없습니다.");
	}

	@DisplayName("authId를 통해 User를 찾는다.")
	@Test
	void findUserByAuthId() {
		//given
		given(findUserPort.findUserByAuthId("tester00")).willReturn(Optional.of(user));

		//when
		User userByAuthId = findUserService.findUserByAuthId("tester00");

		//test
		assertThat(userByAuthId)
			.extracting("id", "authId", "password")
			.contains(1L, "tester00", "tester00!");
	}

	@DisplayName("존재하지 않는 authId일 경우 예외가 발생한다.")
	@Test
	void findUserByNotExistAuthId() {
		//given
		given(findUserPort.findUserByAuthId("tester00")).willReturn(Optional.empty());

		//when //test
		assertThatThrownBy(() -> findUserService.findUserByAuthId("tester00"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 사용자를 찾을 수 없습니다.");

	}

	@DisplayName("학번을 통해 유저를 찾는다.")
	@Test
	void findUserByStudentNumber() {
		//given
		given(findUserPort.findUserByStudentNumber("60191656")).willReturn(Optional.of(user));

		//when
		User user = findUserService.findUserByStudentNumber("60191656");

		//then
		assertThat(user.getStudentNumber()).isEqualTo("60191656");
	}

	@DisplayName("해당 학번의 유저가 존재하지 않을 시 예외가 발생한다.")
	@Test
	void findNonUserByStudentNumber() {
		//given
		given(findUserPort.findUserByStudentNumber("")).willThrow(new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

		//when //then
		assertThatThrownBy(() -> findUserService.findUserByStudentNumber("")).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 사용자를 찾을 수 없습니다.");
	}
}
