package com.plzgraduate.myongjigraduatebe.lecture.application.service.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.SearchLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.service.SearchLectureService;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.SearchedLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchLectureServiceTest {

	@Mock
	private SearchLecturePort searchLecturePort;
	@Mock
	private FindTakenLectureUseCase findTakenLectureUseCase;
	@InjectMocks
	private SearchLectureService searchLectureService;

	@DisplayName("과목을 검색한다.")
	@Test
	void searchLectures() {
		Long userId = 1L;
		String type = "name";
		String keyword = "기초";
		Lecture takenLecture = createLecture(1L, "code1", "기초웹프로그래밍", 3, 0);
		Lecture nonTakenLecture = createLecture(2L, "code2", "앱과웹기초", 2, 1);
		List<Lecture> lectures = List.of(takenLecture, nonTakenLecture);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(
			Set.of(TakenLecture.builder()
				.lecture(Lecture.from("code1"))
				.build())
		);
		given(searchLecturePort.searchLectureByNameOrCode("name", "기초"))
			.willReturn(lectures);
		given(findTakenLectureUseCase.findTakenLectures(userId)).willReturn(takenLectureInventory);

		//when
		List<SearchedLectureDto> searchedLectureDtos = searchLectureService.searchLectures(userId,
			type,
			keyword);

		//then
		assertThat(searchedLectureDtos)
			.hasSize(2)
			.extracting("addable", "lecture")
			.containsExactlyInAnyOrder(
				tuple(true, takenLecture),
				tuple(false, nonTakenLecture)
			);
	}

	private Lecture createLecture(Long id, String lectureCode, String name, int credit,
		int isRevoked) {
		return Lecture.builder()
			.id(id)
			.lectureCode(lectureCode)
			.name(name)
			.credit(credit)
			.isRevoked(isRevoked)
			.build();
	}
}
