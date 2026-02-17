package com.plzgraduate.myongjigraduatebe.user.application.service.withdraw;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.INCORRECT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.DeleteCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.DeleteParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.DeleteUserTimetablePort;
import com.plzgraduate.myongjigraduatebe.user.application.port.DeleteUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class WithDrawUserServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private DeleteTakenLectureUseCase deleteTakenLectureByUserUseCase;
	@Mock
	private DeleteParsingTextHistoryPort deleteParsingTextHistoryPort;
	@Mock
	private DeleteUserTimetablePort deleteUserTimetablePort;
	@Mock
	private DeleteUserPort deleteUserPort;
	@Mock
	private DeleteCompletedCreditPort deleteCompletedCreditPort;
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private WithDrawUserService withDrawUserService;

	@DisplayName("유저 정보를 삭제한다.")
	@Test
	void withDraw() {
		//given
		String password = "abcd1234!";
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = User.builder()
				.id(1L)
				.password(encoder.encode(password))
				.build();
		given(findUserUseCase.findUserById(user.getId())).willReturn(user);
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

		//when
		withDrawUserService.withDraw(user.getId(), password);

		//then
		then(deleteTakenLectureByUserUseCase).should()
				.deleteAllTakenLecturesByUser(user);
		then(deleteParsingTextHistoryPort).should()
				.deleteUserParsingTextHistory(user);
		then(deleteCompletedCreditPort).should()
				.deleteAllCompletedCredits(user);
		then(deleteUserTimetablePort).should()
				.deleteAllByUser(user);
		then(deleteUserPort).should()
				.deleteUser(user);
	}

	@DisplayName("시간표 삭제가 User 삭제보다 먼저 호출된다.")
	@Test
	void withDrawDeletesTimeTableBeforeUser() {
		//given
		String password = "abcd1234!";
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = User.builder()
				.id(1L)
				.password(encoder.encode(password))
				.build();
		given(findUserUseCase.findUserById(user.getId())).willReturn(user);
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

		//when
		withDrawUserService.withDraw(user.getId(), password);

		//then - 순서 검증 (FK 제약조건 때문에 중요!)
		InOrder inOrder = inOrder(deleteUserTimetablePort, deleteUserPort);
		inOrder.verify(deleteUserTimetablePort).deleteAllByUser(user);
		inOrder.verify(deleteUserPort).deleteUser(user);
	}

	@DisplayName("잘못된 비밀번호를 입력하면 예외가 발생한다.")
	@Test
	void withDrawWithUnValidationPassword() {
		//given
		String password = "abcd1234!";
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = User.builder()
				.id(1L)
				.password(encoder.encode(password))
				.build();
		given(findUserUseCase.findUserById(user.getId())).willReturn(user);
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

		//when //then
		assertThatThrownBy(() -> withDrawUserService.withDraw(user.getId(), password))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(INCORRECT_PASSWORD.toString());
	}
}
