package com.plzgraduate.myongjigraduatebe.lecture.application.service.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.SearchLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.service.SearchLectureService;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

@ExtendWith(MockitoExtension.class)
class SearchLectureServiceTest {
	@Mock
	private SearchLecturePort searchLecturePort;
	@InjectMocks
	private SearchLectureService searchLectureService;

	@DisplayName("과목을 검색한다.")
	@Test
	void searchLectures() {
		String type = "name";
		String keyword = "기초";
		List<Lecture> lectures = List.of(
			createLecture(1L, "code1", "기초웹프로그래밍", 3, 0),
			createLecture(2L, "code2", "앱과웹기초", 2, 1)
		);
		given(searchLecturePort.searchLectureByNameOrCode("name", "기초"))
			.willReturn(lectures);

		//when
		List<Lecture> searchedLectures = searchLectureService.searchLectures(type, keyword);

		//then
		assertThat(searchedLectures)
			.hasSize(2)
			.extracting("id", "lectureCode", "name", "credit", "isRevoked")
			.containsExactlyInAnyOrder(
				tuple(1L, "code1", "기초웹프로그래밍", 3, 0),
				tuple(2L, "code2", "앱과웹기초", 2, 1)
			);
	}

	private Lecture createLecture(Long id, String lectureCode, String name, int credit, int isRevoked) {
		return Lecture.builder()
			.id(id)
			.lectureCode(lectureCode)
			.name(name)
			.credit(credit)
			.isRevoked(isRevoked)
			.build();
	}
}
