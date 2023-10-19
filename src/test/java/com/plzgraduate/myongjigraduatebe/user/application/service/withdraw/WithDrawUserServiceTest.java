package com.plzgraduate.myongjigraduatebe.user.application.service.withdraw;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.parsing.application.port.out.DeleteParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.delete.DeleteTakenLectureByUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.DeleteUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class WithDrawUserServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private DeleteTakenLectureByUserUseCase deleteTakenLectureByUserUseCase;
	@Mock
	private DeleteParsingTextHistoryPort deleteParsingTextHistoryPort;
	@Mock
	private DeleteUserPort deleteUserPort;

	@InjectMocks
	private WithDrawUserService withDrawUserService;

	@DisplayName("유저 정보를 삭제한다.")
	@Test
	void withDraw() {
	    //given
		User user = User.builder()
			.id(1L).build();
		given(findUserUseCase.findUserById(user.getId())).willReturn(user);

		//when //then
	    withDrawUserService.withDraw(user.getId());
		then(deleteTakenLectureByUserUseCase).should().deleteAllTakenLecturesByUser(user);
		then(deleteParsingTextHistoryPort).should().deleteUserParsingTextHistory(user);
		then(deleteUserPort).should().deleteUser(user);
	}

}