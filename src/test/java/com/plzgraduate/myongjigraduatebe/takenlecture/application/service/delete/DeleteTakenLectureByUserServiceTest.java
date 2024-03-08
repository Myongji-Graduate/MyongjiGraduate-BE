package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class DeleteTakenLectureByUserServiceTest {

	@Mock
	private DeleteTakenLecturePort deleteTakenLecturePort;
	@InjectMocks
	private DeleteTakenLectureByUserService deleteTakenLectureByUserService;

	@DisplayName("사용자의 모든 수강과목을 삭제한다.")
	@Test
	void deleteAllTakenLecturesByUser() {
		//given
		User user = User.builder().build();

		//when
		deleteTakenLectureByUserService.deleteAllTakenLecturesByUser(user);

		//then
		then(deleteTakenLecturePort).should().deleteAllTakenLecturesByUser(eq(user));
	}
}
