package com.plzgraduate.myongjigraduatebe.lecture.application.service.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.service.FindLecturesService;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindLecturesByIdServiceTest {

	@Mock
	private FindLecturePort findLecturePort;

	@InjectMocks
	private FindLecturesService findLecturesByIdService;

	@DisplayName("과목 아이디 리스트에 포함되는 과목들을 반환한다.")
	@Test
	void test() {
		//given
		List<Long> lectureIds = List.of(1L, 2L, 3L);
		List<Lecture> lectures = List.of(
			createLecture(1L, "KMA00101", "성서와 인간이해", 2, 0, null),
			createLecture(2L, "KMA02104", "글쓰기", 3, 0, null),
			createLecture(3L, "KMA02106", "영어1", 2, 0, null)
		);

		given(findLecturePort.findLecturesByIds(lectureIds)).willReturn(lectures);

		//when
		List<Lecture> result = findLecturesByIdService.findLecturesByIds(lectureIds);

		//then
		assertThat(result).isEqualTo(lectures);
	}

	private Lecture createLecture(Long id, String lectureCode, String name, int credit,
		int isRevoked, String duplicateCode) {
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
