package com.plzgraduate.myongjigraduatebe.user.application.service;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.command.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class UpdateStudentInformationService implements UpdateStudentInformationUseCase {
	private final UpdateUserPort updateUserPort;
	@Override
	public void updateUser(UpdateStudentInformationCommand updateStudentInformationCommand) {
		User user = updateStudentInformationCommand.getUser();
		user.updateStudentInformation(updateStudentInformationCommand);
		updateUserPort.updateUser(user);
	}
}
