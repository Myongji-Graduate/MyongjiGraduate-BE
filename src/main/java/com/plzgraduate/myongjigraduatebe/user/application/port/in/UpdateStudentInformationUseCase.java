package com.plzgraduate.myongjigraduatebe.user.application.port.in;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.command.UpdateStudentInformationCommand;

public interface UpdateStudentInformationUseCase {
	void updateUser(UpdateStudentInformationCommand updateStudentInformationCommand);
}
