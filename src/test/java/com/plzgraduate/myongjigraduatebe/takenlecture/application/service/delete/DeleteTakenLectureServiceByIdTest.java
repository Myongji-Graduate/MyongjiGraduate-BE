package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;

@ExtendWith(MockitoExtension.class)
class DeleteTakenLectureServiceByIdTest {

	@Mock
	private FindUserUseCase findUserUseCase;

	@Mock
	private DeleteTakenLecturePort deleteTakenLecturePort;

	@Mock
	private GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;

	@InjectMocks
	private DeleteTakenLectureServiceById deleteTakenLectureServiceById;

	@DisplayName("수강과목을 삭제한다.")
	@Test
	void deleteTakenLecture() {
		//given
		Long userId = 1L;
		Long deletedTakenLectureId = 102L;

		//when
		deleteTakenLectureServiceById.deleteTakenLecture(userId, deletedTakenLectureId);

		//then
		then(deleteTakenLecturePort).should().deleteTakenLectureById(102L);
	}
}