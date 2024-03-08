package com.plzgraduate.myongjigraduatebe.user.application.service.signup;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.signup.SignUpUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.signup.SignUpCommand;
import com.plzgraduate.myongjigraduatebe.user.application.port.CheckUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class SignUpService implements SignUpUseCase {

	private static final int CLASS_OF_2016 = 16;

	private final SaveUserPort saveUserPort;
	private final CheckUserPort checkUserPort;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void signUp(SignUpCommand signUpCommand) {
		checkDuplicateUser(signUpCommand);
		String encodedPassword = passwordEncoder.encode(signUpCommand.getPassword());
		User newUser = User.create(signUpCommand.getAuthId(), encodedPassword, signUpCommand.getEngLv(),
			signUpCommand.getStudentNumber());
		checkStudentNumberOver16(newUser);
		saveUserPort.saveUser(newUser);
	}

	private void checkStudentNumberOver16(User user) {
		if(user.checkBeforeEntryYear(CLASS_OF_2016)) {
			throw new IllegalArgumentException("서비스 이용대상자는 16~23학번까지입니다.");
		}
	}

	private void checkDuplicateUser(SignUpCommand signUpCommand) {
		if(checkUserPort.checkDuplicateAuthId(signUpCommand.getAuthId())) {
			throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
		}
		if(checkUserPort.checkDuplicateStudentNumber(signUpCommand.getStudentNumber())) {
			throw new IllegalArgumentException("이미 존재하는 학번입니다.");
		}
	}
}
