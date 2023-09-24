package com.plzgraduate.myongjigraduatebe.user.application.service.validate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.validate.ValidateUserResponse;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class ValidateUserServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;

	@InjectMocks
	private ValidateUserService validateUserService;

	@DisplayName("성공: 학번으로 유저를 조회 후 로그인 아이디가 맞는지 검증한다.")
	@Test
	void validatePassedUser() {
	    //given
		String studentNumber = "60191656";
		String authId = "testAuthId";
		User user = User.builder()
			.authId(authId).build();
		given(findUserUseCase.findUserByStudentNumber(studentNumber)).willReturn(user);

	    //when
		ValidateUserResponse validateUserResponse = validateUserService.validateUser(studentNumber, authId);

		//then
		assertThat(validateUserResponse.isPassedUserValidation()).isTrue();
	}

	@DisplayName("실패: 학번으로 유저를 조회 후 로그인 아이디가 맞는지 검증한다.")
	@Test
	void validateNonPassedUser() {
		//given
		String studentNumber = "60191656";
		String authId = "testAuthId";
		User user = User.builder()
			.authId("userAuthId").build();
		given(findUserUseCase.findUserByStudentNumber(studentNumber)).willReturn(user);

		//when
		ValidateUserResponse validateUserResponse = validateUserService.validateUser(studentNumber, authId);

		//then
		assertThat(validateUserResponse.isPassedUserValidation()).isFalse();
	}

}
