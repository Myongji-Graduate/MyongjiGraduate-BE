package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindCompletedCreditServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindCompletedCreditPort findCompletedCreditPort;
	@InjectMocks
	private FindCompletedCreditService findCompletedCreditService;

	@DisplayName("유저의 기이수 학점을 조회한다.")
	@Test
	void findCompletedCredits() {
		//given
		User user = User.builder()
			.id(1L)
			.build();
		given(findUserUseCase.findUserById(1L)).willReturn(user);
		given(findCompletedCreditPort.findCompletedCredit(user)).willReturn(List.of(
			CompletedCredit.builder()
				.build(),
			CompletedCredit.builder()
				.build(),
			CompletedCredit.builder()
				.build()
		));

		//when
		List<CompletedCredit> result = findCompletedCreditService.findCompletedCredits(1L);

		//then
		assertThat(result).hasSize(3);
	}

}
