package com.plzgraduate.myongjigraduatebe.user.application.service.update;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.port.UpdateUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

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
		then(updateUserPort).should().updateUser(user);
	}
}
