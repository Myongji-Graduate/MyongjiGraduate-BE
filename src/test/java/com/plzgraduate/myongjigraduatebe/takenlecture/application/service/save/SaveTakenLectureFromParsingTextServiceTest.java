package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.find.FindLecturesByLectureCodeUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save.SaveTakenLectureCommand;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class SaveTakenLectureFromParsingTextServiceTest {

	@Mock
	private SaveTakenLecturePort saveTakenLecturePort;
	@Mock
	private FindLecturesByLectureCodeUseCase findLecturesByLectureCodeUseCase;
	@InjectMocks
	private SaveTakenLectureFromParsingTextService saveTakenLectureFromParsingTextService;

	@DisplayName("파싱 정보를 사용해 수강과목들을 추가한다.")
	@Test
	void saveTakenLectures() {
		//given
		User user = User.builder().id(1L).build();
		List<SaveTakenLectureCommand.TakenLectureInformation> takenLectureInformationList = new ArrayList<>(List.of(
			createTakenLectureInformation("KMA02122", 2022, Semester.FIRST),
			createTakenLectureInformation("KMA02135", 2023, Semester.FIRST)
		));
		SaveTakenLectureCommand command = SaveTakenLectureCommand.builder()
			.user(user)
			.takenLectureInformationList(takenLectureInformationList)
			.build();
		Lecture lecture1 = createLecture("KMA02122");
		Lecture lecture2 = createLecture("KMA02135");
		given(findLecturesByLectureCodeUseCase.findLecturesByLectureCodes(List.of("KMA02122", "KMA02135")))
			.willReturn(List.of(lecture1, lecture2));
		ArgumentCaptor<List<TakenLecture>> takenLectureListCaptor = ArgumentCaptor.forClass(List.class);

		//when
		saveTakenLectureFromParsingTextService.saveTakenLectures(command);

		//then
		then(saveTakenLecturePort).should().saveTakenLectures(takenLectureListCaptor.capture());
		List<TakenLecture> capturesTakenLectures = takenLectureListCaptor.getValue();
		assertThat(capturesTakenLectures)
			.hasSize(2)
			.extracting("year", "semester")
			.containsExactlyInAnyOrder(
				tuple(2022, Semester.FIRST),
				tuple(2023, Semester.FIRST)
			);
	}

	@DisplayName("과목 데이터베이스에 존재하지 않는 과목일 경우 예외를 반환한다.")
	@Test
	void lectureDoesNotExist() {
		//given
		User user = User.builder().id(1L).build();
		List<SaveTakenLectureCommand.TakenLectureInformation> takenLectureInformationList = new ArrayList<>(List.of(
			createTakenLectureInformation("KMA02122", 2022, Semester.FIRST),
			createTakenLectureInformation("KMA02135", 2023, Semester.FIRST)
		));
		SaveTakenLectureCommand command = SaveTakenLectureCommand.builder()
			.user(user)
			.takenLectureInformationList(takenLectureInformationList)
			.build();
		Lecture lecture1 = createLecture("KMA02122");
		given(findLecturesByLectureCodeUseCase.findLecturesByLectureCodes(List.of("KMA02122", "KMA02135")))
			.willReturn(List.of(lecture1));
		ArgumentCaptor<List<TakenLecture>> takenLectureListCaptor = ArgumentCaptor.forClass(List.class);

		//when //then
		assertThatThrownBy(() -> saveTakenLectureFromParsingTextService.saveTakenLectures(command))
			.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("KMA02135이 데이터베이스에 존재하지 않습니다.");

	}

	private SaveTakenLectureCommand.TakenLectureInformation createTakenLectureInformation(
		String lectureCode, int year, Semester semester
	) {
		return SaveTakenLectureCommand.TakenLectureInformation.builder()
			.lectureCode(lectureCode)
			.year(year)
			.semester(semester)
			.build();
	}

	private Lecture createLecture(String lectureCode) {
		return Lecture.builder()
			.lectureCode(lectureCode)
			.build();
	}
}
