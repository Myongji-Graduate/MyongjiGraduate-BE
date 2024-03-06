package com.plzgraduate.myongjigraduatebe.user.application.service.check;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.StudentNumberDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.CheckUserPort;

@ExtendWith(MockitoExtension.class)
class CheckStudentNumberDuplicationServiceTest {

	@Mock
	private CheckUserPort checkUserPort;

	@InjectMocks
	private CheckStudentNumberDuplicationService checkStudentNumberDuplicationService;

	@DisplayName("학번의 중복 여부를 확인한다.")
	@Test
	void checkStudentNumberDuplication() {
		//given
		String studentNumber = "60191656";
		boolean studentNumberDuplication = true;
		given(checkUserPort.checkDuplicateStudentNumber(studentNumber)).willReturn(studentNumberDuplication);

		//when
		StudentNumberDuplicationResponse studentNumberDuplicationResponse = checkStudentNumberDuplicationService.checkStudentNumberDuplication(
			studentNumber);

		//then
		assertThat(studentNumberDuplicationResponse).extracting("studentNumber", "notDuplicated")
			.contains(studentNumber, !studentNumberDuplication);
	}

}
