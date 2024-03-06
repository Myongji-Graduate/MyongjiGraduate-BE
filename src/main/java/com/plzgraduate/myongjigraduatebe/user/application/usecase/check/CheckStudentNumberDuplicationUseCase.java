package com.plzgraduate.myongjigraduatebe.user.application.usecase.check;

import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.StudentNumberDuplicationResponse;

public interface CheckStudentNumberDuplicationUseCase {

	StudentNumberDuplicationResponse checkStudentNumberDuplication(String studentNumber);
}
