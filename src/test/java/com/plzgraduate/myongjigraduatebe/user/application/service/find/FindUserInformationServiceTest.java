package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import static org.assertj.core.api.Assertions.assertThat;
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
class FindUserInformationServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;

	@InjectMocks
	private FindUserInformationService findUserInformationService;

	@DisplayName("학생 정보결과를 반환한다.")
	@Test
	void findUserInformation() {
	    //given
		Long userId = 1L;
		String studentName = "testUser";
		String studentNumber = "11111111";
		String major = "testMajor";

		User foundUser = User.builder()
			.id(userId)
			.name(studentName)
			.studentNumber(studentNumber)
			.major(major).build();

		given(findUserUseCase.findUserById(userId)).willReturn(foundUser);

	    //when
		User user = findUserInformationService.findUserInformation(userId);

		//then
		assertThat(user).isEqualTo(foundUser);
	}

}
