package com.plzgraduate.myongjigraduatebe.user.application.usecase.update;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface UpdateStudentInformationUseCase {
	User updateUser(UpdateStudentInformationCommand updateStudentInformationCommand);
}
