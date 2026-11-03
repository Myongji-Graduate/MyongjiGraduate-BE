package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteTakenLectureServiceTest {

    @Mock
    private com.plzgraduate.myongjigraduatebe.takenlecture.application.port.TakenLectureRevisionPort takenLectureRevisionPort;

	@Mock
	private FindUserUseCase findUserUseCase;

	@Mock
	private DeleteTakenLecturePort deleteTakenLecturePort;

	@Mock
	private GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;

	@Mock
	private FindTakenLecturePort findTakenLecturePort;

	@InjectMocks
	private DeleteTakenLectureService deleteTakenLectureService;

	@Test
	void deleteTakenLecture() {
		//given
		Long userId = 1L;
		Long deletedTakenLectureId = 102L;
		User user = User.builder().id(userId).build();
		TakenLecture takenLecture = TakenLecture.builder()
			.id(deletedTakenLectureId)
			.user(user)
			.build();

		given(findUserUseCase.findUserById(userId)).willReturn(user);
		given(findTakenLecturePort.findTakenLecturesByUser(user)).willReturn(List.of(takenLecture));

		//when
		deleteTakenLectureService.deleteTakenLecture(userId, deletedTakenLectureId);

		//then
        var changes = inOrder(takenLectureRevisionPort, findTakenLecturePort,
            deleteTakenLecturePort, generateOrModifyCompletedCreditUseCase);
        changes.verify(takenLectureRevisionPort).lockForChange(userId);
        changes.verify(findTakenLecturePort).findTakenLecturesByUser(user);
        changes.verify(deleteTakenLecturePort).deleteTakenLectureById(deletedTakenLectureId);
        changes.verify(generateOrModifyCompletedCreditUseCase).generateOrModifyCompletedCredit(user);
	}

    @DisplayName("사용자의 이수 목록에 없는 과목은 삭제하거나 학점을 재계산하지 않는다.")
    @Test
    void rejectsLectureOutsideUsersInventory() {
        User user = User.builder().id(1L).build();
        TakenLecture ownLecture = TakenLecture.builder().id(101L).user(user).build();
        given(findUserUseCase.findUserById(user.getId())).willReturn(user);
        given(findTakenLecturePort.findTakenLecturesByUser(user)).willReturn(List.of(ownLecture));

        assertThatThrownBy(() -> deleteTakenLectureService.deleteTakenLecture(user.getId(), 102L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("102");

        verifyNoInteractions(deleteTakenLecturePort, generateOrModifyCompletedCreditUseCase);
    }

    @DisplayName("과목 삭제에 실패하면 학점 재계산을 호출하지 않는다.")
    @Test
    void failedDeleteDoesNotRecalculateCredit() {
        User user = User.builder().id(1L).build();
        TakenLecture takenLecture = TakenLecture.builder().id(102L).user(user).build();
        given(findUserUseCase.findUserById(user.getId())).willReturn(user);
        given(findTakenLecturePort.findTakenLecturesByUser(user)).willReturn(List.of(takenLecture));
        var failure = new IllegalStateException("synthetic delete failure");
        willThrow(failure).given(deleteTakenLecturePort).deleteTakenLectureById(takenLecture.getId());

        assertThatThrownBy(() ->
            deleteTakenLectureService.deleteTakenLecture(user.getId(), takenLecture.getId()))
            .isSameAs(failure);

        verifyNoInteractions(generateOrModifyCompletedCreditUseCase);
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
