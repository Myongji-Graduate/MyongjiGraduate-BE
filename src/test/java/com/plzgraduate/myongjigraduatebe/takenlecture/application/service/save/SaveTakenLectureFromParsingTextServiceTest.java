package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.NON_EXISTED_LECTURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaveTakenLectureFromParsingTextServiceTest {

	@Mock
	private SaveTakenLecturePort saveTakenLecturePort;
	@Mock
	private FindLecturesUseCase findLecturesUseCase;
	@InjectMocks
	private SaveTakenLectureFromParsingTextService saveTakenLectureFromParsingTextService;

	@DisplayName("파싱 정보를 사용해 수강과목들을 추가한다.")
	@Test
	void saveTakenLectures() {
		//given
		User user = User.builder()
			.id(1L)
			.build();
		List<TakenLectureInformation> takenLectureInformationList = new ArrayList<>(List.of(
			createTakenLectureInformation("KMA02122", 2022),
			createTakenLectureInformation("KMA02135", 2023)
		));
		Lecture lecture1 = createLecture("KMA02122");
		Lecture lecture2 = createLecture("KMA02135");
		given(findLecturesUseCase.findLecturesByLectureCodes(List.of("KMA02122", "KMA02135")))
			.willReturn(List.of(lecture1, lecture2));
		ArgumentCaptor<List<TakenLecture>> takenLectureListCaptor = ArgumentCaptor.forClass(
			List.class);

		//when
		saveTakenLectureFromParsingTextService.saveTakenLectures(user, takenLectureInformationList);

		//then
		then(saveTakenLecturePort).should()
			.saveTakenLectures(takenLectureListCaptor.capture());
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
		User user = User.builder()
			.id(1L)
			.build();
		List<TakenLectureInformation> takenLectureInformationList = new ArrayList<>(List.of(
			createTakenLectureInformation("KMA02122", 2022),
			createTakenLectureInformation("KMA02135", 2023)
		));
		Lecture lecture1 = createLecture("KMA02122");
		given(findLecturesUseCase.findLecturesByLectureCodes(List.of("KMA02122", "KMA02135")))
			.willReturn(List.of(lecture1));

		//when //then
		assertThatThrownBy(
			() -> saveTakenLectureFromParsingTextService.saveTakenLectures(user,
				takenLectureInformationList))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage(NON_EXISTED_LECTURE.toString());

	}

	private TakenLectureInformation createTakenLectureInformation(String lectureCode, int year) {
		return TakenLectureInformation.builder()
			.lectureCode(lectureCode)
			.year(year)
			.semester(Semester.FIRST)
			.build();
	}

	private Lecture createLecture(String lectureCode) {
		return Lecture.builder()
			.lectureCode(lectureCode)
			.build();
	}
}
