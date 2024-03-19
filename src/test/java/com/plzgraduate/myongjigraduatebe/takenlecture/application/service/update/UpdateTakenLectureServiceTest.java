package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class UpdateTakenLectureServiceTest {
	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindLecturesUseCase findLecturesUseCase;
	@Mock
	private DeleteTakenLecturePort deleteTakenLecturePort;
	@Mock
	private SaveTakenLecturePort saveTakenLecturePort;
	@InjectMocks
	private UpdateTakenLectureService updateTakenLectureService;

	@DisplayName("수강과목을 삭제하고 새로운 수강정보를 추가한다.")
	@Test
	void modifyTakenLecture() {
		//given
		User user = User.builder().id(1L).build();
		Lecture lecture1 = createLecture(1L);
		Lecture lecture2 = createLecture(2L);
		given(findLecturesUseCase.findLecturesByIds(List.of(1L, 2L)))
			.willReturn(new ArrayList<>(List.of(lecture1, lecture2)));

		ArgumentCaptor<List<TakenLecture>> takenLectureListCaptor = ArgumentCaptor.forClass(List.class);
		//when
		updateTakenLectureService.modifyTakenLecture(user.getId(), List.of(21L, 22L), List.of(1L, 2L));

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
