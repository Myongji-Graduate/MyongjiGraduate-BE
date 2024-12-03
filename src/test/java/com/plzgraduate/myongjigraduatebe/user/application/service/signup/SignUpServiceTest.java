package com.plzgraduate.myongjigraduatebe.user.application.service.signup;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.DUPLICATED_AUTHID;
import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.DUPLICATED_STUDENT_NUMBER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.plzgraduate.myongjigraduatebe.user.application.port.CheckUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.SaveUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.signup.SignUpCommand;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
		then(passwordEncoder).should()
			.encode("1q2w3e4r!");
		then(saveUserPort).should()
			.saveUser(any(User.class));
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
			.hasMessage(DUPLICATED_AUTHID.toString());

		then(checkUserPort).should(times(1))
			.checkDuplicateAuthId(authId);
		then(checkUserPort).should(never())
			.checkDuplicateStudentNumber(anyString());
		then(saveUserPort).should(never())
			.saveUser(any(User.class));
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
			.hasMessage(DUPLICATED_STUDENT_NUMBER.toString());

		then(checkUserPort).should(times(1))
			.checkDuplicateAuthId(anyString());
		then(checkUserPort).should(times(1))
			.checkDuplicateStudentNumber(studentNumber);
		then(saveUserPort).should(never())
			.saveUser(any(User.class));
	}

}
