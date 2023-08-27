package com.plzgraduate.myongjigraduatebe.user.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.command.SignUpCommand;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.CheckUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {
	@Mock
	private SaveUserPort saveUserPort;
	@Mock
	private CheckUserPort checkUserPort;
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

	@DisplayName("가입하려는 아이디가 이미 존재한다.")
	@Test
	void 중복아이디_회원가입() {
		//given
		String authId = "mju-graduate";
		SignUpCommand command = SignUpCommand.builder()
			.authId(authId)
			.password("1q2w3e4r!")
			.studentNumber("60201000")
			.engLv(EnglishLevel.ENG12)
			.build();
		given(checkUserPort.checkDuplicateAuthId(anyString())).willReturn(true);

		//when //then
		assertThatThrownBy(() -> signUpService.signUp(command))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("이미 존재하는 아이디입니다.");

		then(checkUserPort).should(times(1)).checkDuplicateAuthId(authId);
		then(checkUserPort).should(never()).checkDuplicateStudentNumber(anyString());
		then(saveUserPort).should(never()).saveUser(any(User.class));
	}

	@DisplayName("가입하려는 학번이 이미 존재한다.")
	@Test
	void 중복학번_회원가입() {
		//given
		String studentNumber = "60201000";
		SignUpCommand command = SignUpCommand.builder()
			.authId("mju-graduate")
			.password("1q2w3e4r!")
			.studentNumber("60201000")
			.engLv(EnglishLevel.ENG12)
			.build();
		given(checkUserPort.checkDuplicateAuthId(anyString())).willReturn(false);
		given(checkUserPort.checkDuplicateStudentNumber(anyString())).willReturn(true);

		//when //then
		assertThatThrownBy(() -> signUpService.signUp(command))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("이미 존재하는 학번입니다.");

		then(checkUserPort).should(times(1)).checkDuplicateAuthId(anyString());
		then(checkUserPort).should(times(1)).checkDuplicateStudentNumber(studentNumber);
		then(saveUserPort).should(never()).saveUser(any(User.class));
	}

}
