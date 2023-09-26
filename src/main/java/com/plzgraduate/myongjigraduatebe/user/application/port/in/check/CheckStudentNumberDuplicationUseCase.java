package com.plzgraduate.myongjigraduatebe.user.application.port.in.check;

public interface CheckStudentNumberDuplicationUseCase {

	StudentNumberDuplicationResponse checkStudentNumberDuplication(String studentNumber);
}
