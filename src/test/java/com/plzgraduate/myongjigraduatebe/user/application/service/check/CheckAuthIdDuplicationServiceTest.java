package com.plzgraduate.myongjigraduatebe.user.application.service.check;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.AuthIdDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.CheckUserPort;

@ExtendWith(MockitoExtension.class)
class CheckAuthIdDuplicationServiceTest {

	@Mock
	private CheckUserPort checkUserPort;

	@InjectMocks
	private CheckAuthIdDuplicationService checkAuthIdDuplicationService;

	@DisplayName("로그인 아이디의 중복 여부를 확인한다.")
	@Test
	void checkAuthIdDuplication() {
	    //given
		String authId = "testAuthId";
		boolean authIdDuplication = true;
		given(checkUserPort.checkDuplicateAuthId(authId)).willReturn(authIdDuplication);

	    //when
		AuthIdDuplicationResponse authIdDuplicationResponse = checkAuthIdDuplicationService.checkAuthIdDuplication(
			authId);

		//then
		assertThat(authIdDuplicationResponse).extracting("authId", "isNotDuplicated")
			.contains(authId, !authIdDuplication);
	}

}
