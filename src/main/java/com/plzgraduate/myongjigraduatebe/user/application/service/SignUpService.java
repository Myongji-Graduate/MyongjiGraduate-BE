package com.plzgraduate.myongjigraduatebe.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.SignUpUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.command.SignUpCommand;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
class SignUpService implements SignUpUseCase {

	private final SaveUserPort saveUserPort;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void signUp(SignUpCommand signUpCommand) {
		String encodedPassword = passwordEncoder.encode(signUpCommand.getPassword());
		User newUser = User.create(signUpCommand.getAuthId(), encodedPassword, signUpCommand.getEngLv(),
			signUpCommand.getStudentNumber());
		saveUserPort.saveUser(newUser);
	}
}
