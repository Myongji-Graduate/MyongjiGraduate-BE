package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.then;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteTakenLectureServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;

	@Mock
	private DeleteTakenLecturePort deleteTakenLecturePort;

	@Mock
	private GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;

	@InjectMocks
	private DeleteTakenLectureService deleteTakenLectureService;

	@DisplayName("수강과목을 삭제한다.")
	@Test
	void deleteTakenLecture() {
		//given
		Long userId = 1L;
		Long deletedTakenLectureId = 102L;

		//when
		deleteTakenLectureService.deleteTakenLecture(userId, deletedTakenLectureId);

		//then
		then(deleteTakenLecturePort).should()
			.deleteTakenLectureById(102L);
	}

	@DisplayName("사용자의 모든 수강과목을 삭제한다.")
	@Test
	void deleteAllTakenLecturesByUser() {
		//given
		User user = User.builder()
			.build();

		//when
		deleteTakenLectureService.deleteAllTakenLecturesByUser(user);

		//then
		then(deleteTakenLecturePort).should()
			.deleteAllTakenLecturesByUser(eq(user));
	}
}
