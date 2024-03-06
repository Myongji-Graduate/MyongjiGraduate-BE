package com.plzgraduate.myongjigraduatebe.user.application.service.resetpassword;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.resetpassword.ResetPasswordCommand;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.resetpassword.ResetPasswordUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.UpdateUserPort;
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
	public void resetPassword(ResetPasswordCommand resetPasswordCommand) {
		checkMatchPassword(resetPasswordCommand);
		User user = findUserUseCase.findUserByAuthId(resetPasswordCommand.getAuthId());
		user.resetPassword(passwordEncoder.encode(resetPasswordCommand.getNewPassword()));
		updateUserPort.updateUser(user);
	}

	private static void checkMatchPassword(ResetPasswordCommand resetPasswordCommand) {
		if (!resetPasswordCommand.getNewPassword().equals(resetPasswordCommand.getPasswordCheck())) {
			throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		}
	}
}
