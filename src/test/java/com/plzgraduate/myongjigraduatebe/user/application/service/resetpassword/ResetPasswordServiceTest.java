package com.plzgraduate.myongjigraduatebe.user.application.service.resetpassword;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.resetpassword.ResetPasswordCommand;
import com.plzgraduate.myongjigraduatebe.user.application.port.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private UpdateUserPort updateUserPort;
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private ResetPasswordService resetPasswordService;

	@DisplayName("유저의 아이디로 조회 후 새로운 비밀번호로 변경한다.")
	@Test
	void resetPassword() {
		//given
		ResetPasswordCommand resetPasswordCommand = ResetPasswordCommand.builder()
			.authId("test")
			.newPassword("testPassword")
			.passwordCheck("testPassword").build();
		User user = User.builder().build();
		given(findUserUseCase.findUserByAuthId(resetPasswordCommand.getAuthId())).willReturn(user);

		//when
		resetPasswordService.resetPassword(resetPasswordCommand);

		//then
		then(passwordEncoder).should().encode(resetPasswordCommand.getNewPassword());
		then(updateUserPort).should().updateUser(user);
	}

	@DisplayName("변경 비밀번호와 비밀번호 확인이 일치하지 않을 경우 예외가 발생한다.")
	@Test
	void resetPasswordWithDifferentPasswordCheck() {
		//given
		ResetPasswordCommand resetPasswordCommand = ResetPasswordCommand.builder()
			.authId("test")
			.newPassword("testPassword")
			.passwordCheck("differentPassword").build();

		//when //then
		assertThatThrownBy(() -> resetPasswordService.resetPassword(resetPasswordCommand)).isInstanceOf(
				IllegalArgumentException.class)
			.hasMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
	}

	@DisplayName("해당 유저 아이디가 존재하지 않을 경우 예외가 발생한다.")
	@Test
	void resetPasswordNonUser() {
		ResetPasswordCommand resetPasswordCommand = ResetPasswordCommand.builder()
			.authId("test")
			.newPassword("testPassword")
			.passwordCheck("testPassword").build();
		given(findUserUseCase.findUserByAuthId(resetPasswordCommand.getAuthId())).willThrow(
			new IllegalArgumentException("존재하지 않는 아이디입니다."));

		//when //then
		assertThatThrownBy(() -> resetPasswordService.resetPassword(resetPasswordCommand)).isInstanceOf(
				IllegalArgumentException.class)
			.hasMessage("존재하지 않는 아이디입니다.");
	}

}
