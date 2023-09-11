package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

@ExtendWith(MockitoExtension.class)
class FindLecturesByLectureCodeServiceTest {

	@Mock
	private FindLecturePort findLecturePort;

	@InjectMocks
	private FindLecturesByLectureCodeService findLecturesByLectureCodeService;

	@DisplayName("과목 코드 리스트에 포함되는 과목들을 반환한다.")
	@Test
	void findLecturesByLectureCodes() {
	    //given
		List<String> lectureCodes = List.of("KMA00101", "KMA02104", "KMA02106");
		List<Lecture> lectures = List.of(
			createLecture(1L, "KMA00101", "성서와 인간이해", 2, 0, null),
			createLecture(2L, "KMA02104", "글쓰기", 3, 0, null),
			createLecture(3L, "KMA02106", "영어1", 2, 0, null)
		);
		given(findLecturePort.findLecturesByLectureCodes(lectureCodes)).willReturn(lectures);

		//when
		List<Lecture> result = findLecturesByLectureCodeService.findLecturesByLectureCodes(lectureCodes);

		//then
		assertThat(result).isEqualTo(lectures);
	}

	private Lecture createLecture(Long id, String lectureCode, String name, int credit, int isRevoked, String duplicateCode) {
		return Lecture.builder()
			.id(id)
			.lectureCode(lectureCode)
			.name(name)
			.credit(credit)
			.isRevoked(isRevoked)
			.duplicateCode(duplicateCode)
			.build();
	}

}
