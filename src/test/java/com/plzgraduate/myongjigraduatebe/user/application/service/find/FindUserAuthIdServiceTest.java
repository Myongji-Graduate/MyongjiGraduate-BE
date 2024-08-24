package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class FindUserAuthIdServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;

	@InjectMocks
	private FindUserAuthIdService findUserAuthIdService;

	@DisplayName("학번을 통해 해당 학번 학생의 암호화된 로그인 아이디를 얻는다.")
	@Test
	void findUserAuthId() {
		//given
		String studentNumber = "60191111";
		User user = User.builder()
			.authId("tester00")
			.password("tester00!")
			.studentNumber(studentNumber)
			.build();
		given(findUserUseCase.findUserByStudentNumber(anyString())).willReturn(user);

		//when
		String userAuthId = findUserAuthIdService.findUserAuthId(studentNumber);

		//then
		assertThat(userAuthId).isEqualTo("teste***");
	}

	@DisplayName("학번에 해당하는 유저가 존재하지 않을 경우 예외가 발생한다.")
	@Test
	void findUserAuthIdWithoutUser() {
		//given
		String studentNumber = "60191111";
		given(findUserUseCase.findUserByStudentNumber(anyString())).willThrow(
			new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

		//when //then
		assertThatThrownBy(() -> findUserAuthIdService.findUserAuthId(studentNumber))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 사용자를 찾을 수 없습니다.");
	}

}
