package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.parsing.api.TakenLectureCacheEvict;
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
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindLecturePort findLecturePort;
	@Mock
	private SaveTakenLecturePort saveTakenLecturePort;
	@Mock
	private GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;
	@Mock
	private TakenLectureCacheEvict takenLectureCacheEvict;
	@InjectMocks
	private GenerateCustomizedTakenLectureService generateTakenLectureService;

	@DisplayName("새로운 custom 과목을 추가한다.")
	@Test
	void saveTakenLecture() {
		//given
		User user = User.builder()
			.id(1L)
			.build();
		Lecture lecture = createLecture("code");
		given(findLecturePort.findLectureById("code"))
			.willReturn(lecture);

		ArgumentCaptor<TakenLecture> takenLectureCaptor = ArgumentCaptor.forClass(
			TakenLecture.class);

		//when
		generateTakenLectureService.generateCustomizedTakenLecture(user.getId(), "code");
		generateOrModifyCompletedCreditUseCase.generateOrModifyCompletedCredit(user);

		//then
		then(saveTakenLecturePort).should()
			.saveTakenLecture(takenLectureCaptor.capture());
	}

	private Lecture createLecture(String id) {
		return Lecture.builder()
			.id(id)
			.build();
	}
}
