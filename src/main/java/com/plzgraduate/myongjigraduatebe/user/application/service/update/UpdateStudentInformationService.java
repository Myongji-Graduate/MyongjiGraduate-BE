package com.plzgraduate.myongjigraduatebe.user.application.service.update;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
class UpdateStudentInformationService implements UpdateStudentInformationUseCase {

	private final UpdateUserPort updateUserPort;

	@Override
	public User updateUser(UpdateStudentInformationCommand updateStudentInformationCommand) {
		User user = updateStudentInformationCommand.getUser();
		user.updateStudentInformation(updateStudentInformationCommand.getName(),
			updateStudentInformationCommand.getMajor(),
			updateStudentInformationCommand.getDualMajor(),
			updateStudentInformationCommand.getSubMajor(),
			updateStudentInformationCommand.getAssociatedMajor(),
			updateStudentInformationCommand.getTransferStatus(),
			updateStudentInformationCommand.getStudentCategory(),
			updateStudentInformationCommand.getTotalCredit(),
			updateStudentInformationCommand.getTakenCredit(),
			updateStudentInformationCommand.isGraduate()
		);
		updateUserPort.updateUser(user);
		return user;
	}
}
