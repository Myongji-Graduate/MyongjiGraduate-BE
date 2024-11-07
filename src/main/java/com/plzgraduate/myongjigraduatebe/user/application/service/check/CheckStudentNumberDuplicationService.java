package com.plzgraduate.myongjigraduatebe.user.application.service.check;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.StudentNumberDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.CheckUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.check.CheckStudentNumberDuplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CheckStudentNumberDuplicationService implements CheckStudentNumberDuplicationUseCase {

	private final CheckUserPort checkUserPort;

	@Override
	public StudentNumberDuplicationResponse checkStudentNumberDuplication(String studentNumber) {
		boolean studentNumberDuplication = !checkUserPort.checkDuplicateStudentNumber(
			studentNumber);
		return StudentNumberDuplicationResponse.builder()
			.studentNumber(studentNumber)
			.notDuplicated(studentNumberDuplication)
			.build();
	}
}
