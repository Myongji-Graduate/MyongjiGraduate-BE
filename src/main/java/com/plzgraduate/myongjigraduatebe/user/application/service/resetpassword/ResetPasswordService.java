package com.plzgraduate.myongjigraduatebe.user.application.service.resetpassword;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.resetpassword.ResetPasswordUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class ResetPasswordService implements ResetPasswordUseCase {

	private final FindUserUseCase findUserUseCase;
	private final UpdateUserPort updateUserPort;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void resetPassword(String authId, String newPassword, String passwordCheck) {
		checkMatchPassword(newPassword, passwordCheck);
		User user = findUserUseCase.findUserByAuthId(authId);
		user.resetPassword(passwordEncoder.encode(newPassword));
		updateUserPort.updateUser(user);
	}

	private void checkMatchPassword(String newPassword,String passwordCheck) {
		if (!newPassword.equals(passwordCheck)) {
			throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		}
	}
}
