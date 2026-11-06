package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenerateTakenLectureServiceTest {

    @Mock
    private com.plzgraduate.myongjigraduatebe.takenlecture.application.port.TakenLectureRevisionPort takenLectureRevisionPort;

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindLecturePort findLecturePort;
	@Mock
	private SaveTakenLecturePort saveTakenLecturePort;
	@Mock
	private GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;
	@InjectMocks
	private GenerateCustomizedTakenLectureService generateTakenLectureService;

    @DisplayName("사용자의 custom 과목을 저장한 뒤 학점을 재계산한다.")
    @Test
    void saveTakenLecture() {
        User user = User.builder().id(1L).build();
        Lecture lecture = createLecture("code");
        given(findUserUseCase.findUserById(user.getId())).willReturn(user);
        given(findLecturePort.findLectureById("code")).willReturn(lecture);
        ArgumentCaptor<TakenLecture> takenLectureCaptor = ArgumentCaptor.forClass(TakenLecture.class);

        generateTakenLectureService.generateCustomizedTakenLecture(user.getId(), "code");

        var writes = inOrder(takenLectureRevisionPort, saveTakenLecturePort,
            generateOrModifyCompletedCreditUseCase);
        writes.verify(takenLectureRevisionPort).lockForChange(user.getId());
        writes.verify(saveTakenLecturePort).saveTakenLecture(takenLectureCaptor.capture());
        writes.verify(generateOrModifyCompletedCreditUseCase).generateOrModifyCompletedCredit(user);
        assertThat(takenLectureCaptor.getValue().getUser()).isSameAs(user);
        assertThat(takenLectureCaptor.getValue().getLecture()).isSameAs(lecture);
    }

    @DisplayName("과목 저장에 실패하면 학점 재계산을 호출하지 않는다.")
    @Test
    void failedSaveDoesNotRecalculateCredit() {
        User user = User.builder().id(1L).build();
        Lecture lecture = createLecture("code");
        given(findUserUseCase.findUserById(user.getId())).willReturn(user);
        given(findLecturePort.findLectureById("code")).willReturn(lecture);
        var failure = new IllegalStateException("synthetic save failure");
        willThrow(failure).given(saveTakenLecturePort).saveTakenLecture(any(TakenLecture.class));

        assertThatThrownBy(() ->
            generateTakenLectureService.generateCustomizedTakenLecture(user.getId(), "code"))
            .isSameAs(failure);

        verifyNoInteractions(generateOrModifyCompletedCreditUseCase);
    }

	private Lecture createLecture(String id) {
		return Lecture.builder()
			.id(id)
			.build();
	}
}
