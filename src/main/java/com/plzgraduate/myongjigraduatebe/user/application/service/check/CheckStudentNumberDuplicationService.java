package com.plzgraduate.myongjigraduatebe.user.application.service.check;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.check.CheckStudentNumberDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.StudentNumberDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.CheckUserPort;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CheckStudentNumberDuplicationService implements CheckStudentNumberDuplicationUseCase {

	private final CheckUserPort checkUserPort;

	@Override
	public StudentNumberDuplicationResponse checkStudentNumberDuplication(String studentNumber) {
		boolean studentNumberDuplication = !checkUserPort.checkDuplicateStudentNumber(studentNumber);
		return StudentNumberDuplicationResponse.builder()
			.studentNumber(studentNumber)
			.notDuplicated(studentNumberDuplication).build();
	}
}
