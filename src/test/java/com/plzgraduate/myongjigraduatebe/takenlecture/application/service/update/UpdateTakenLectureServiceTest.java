package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.update;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.find.FindLecturesByIdUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.update.UpdateTakenLectureCommand;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class UpdateTakenLectureServiceTest {
	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindLecturesByIdUseCase findLecturesByIdUseCase;
	@Mock
	private DeleteTakenLecturePort deleteTakenLecturePort;
	@Mock
	private SaveTakenLecturePort saveTakenLecturePort;
	@InjectMocks
	private UpdateTakenLectureService updateTakenLectureService;

	@DisplayName("수강과목을 삭제하고 새로운 수강정보를 추가한다.")
	@Test
	void updateTakenLecture() {
		//given
		UpdateTakenLectureCommand command = UpdateTakenLectureCommand.builder()
			.userId(1L)
			.addedTakenLectures(List.of(1L, 2L))
			.deletedTakenLectures(List.of(21L, 22L))
			.build();
		User user = User.builder().id(1L).build();
		Lecture lecture1 = createLecture(1L);
		Lecture lecture2 = createLecture(2L);
		given(findLecturesByIdUseCase.findLecturesByIds(List.of(1L, 2L)))
			.willReturn(new ArrayList<>(List.of(lecture1, lecture2)));

		ArgumentCaptor<List<TakenLecture>> takenLectureListCaptor = ArgumentCaptor.forClass(List.class);
		//when
		updateTakenLectureService.updateTakenLecture(command);

		//then
		then(deleteTakenLecturePort).should().deleteTakenLecturesByIds(List.of(21L, 22L));
		then(saveTakenLecturePort).should().saveTakenLectures(takenLectureListCaptor.capture());

		List<TakenLecture> captureLectures = takenLectureListCaptor.getValue();
		assertThat(captureLectures).hasSize(2)
			.extracting("year", "semester")
			.containsExactlyInAnyOrder(
				tuple(2099, null),
				tuple(2099, null)
			);
	}


	private Lecture createLecture(Long id) {
		return Lecture.builder()
			.id(id)
			.build();
	}
}
