package com.plzgraduate.myongjigraduatebe.user.application.service.update;

import static org.mockito.BDDMockito.then;
import static org.testng.AssertJUnit.assertEquals;

import com.plzgraduate.myongjigraduatebe.user.application.port.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateStudentInformationServiceTest {

	@Mock
	private UpdateUserPort updateUserPort;
	@InjectMocks
	private UpdateStudentInformationService updateStudentInformationService;

	@DisplayName("User의 학생정보를 수정한다.")
	@Test
	void updateUser() {
		//given
		User user = User.builder()
			.build();
		UpdateStudentInformationCommand command = UpdateStudentInformationCommand.builder()
			.user(user)
			.name("정지환")
			.major("응용소프트웨어학과")
			.subMajor("경영학과")
			.studentCategory(StudentCategory.DUAL_MAJOR)
			.build();

		//then
		updateStudentInformationService.updateUser(command);

		//then
		then(updateUserPort).should()
			.updateUser(user);
	}
	@DisplayName("User의 TransferCredit 정보를 수정한다.")
	@Test
	void updateUserTransferCredit() {
		// given
		User user = User.builder()
				.transferCredit(TransferCredit.empty())
				.build();

		TransferCredit newTransferCredit = new TransferCredit(3, 6, 9, 2);

		UpdateStudentInformationCommand command = UpdateStudentInformationCommand.builder()
				.user(user)
				.name("정지환")
				.major("응용소프트웨어학과")
				.studentCategory(StudentCategory.TRANSFER)
				.transferCredit(newTransferCredit)
				.build();

		// when
		updateStudentInformationService.updateUser(command);

		// then
		then(updateUserPort).should()
				.updateUser(user);

		// Assert TransferCredit is updated
		assertEquals(newTransferCredit, user.getTransferCredit());
	}
}
