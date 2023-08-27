package com.plzgraduate.myongjigraduatebe.user.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.command.SignUpCommand;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {
	@Mock
	private SaveUserPort saveUserPort;
	@Mock
	private PasswordEncoder passwordEncoder;
	@InjectMocks
	private SignUpService signUpService;

	@DisplayName("회원가입을 진행한다")
	@Test
	void signUp() {
	    //given
		SignUpCommand command = SignUpCommand.builder()
			.authId("mju-graduate")
			.password("1q2w3e4r!")
			.studentNumber("60201000")
			.engLv(EnglishLevel.ENG12)
			.build();
		given(passwordEncoder.encode("1q2w3e4r!")).willReturn("encodedPassword");

		//when
		signUpService.signUp(command);

	    //then
		then(passwordEncoder).should().encode("1q2w3e4r!");
		then(saveUserPort).should().saveUser(any(User.class));
	}

}
