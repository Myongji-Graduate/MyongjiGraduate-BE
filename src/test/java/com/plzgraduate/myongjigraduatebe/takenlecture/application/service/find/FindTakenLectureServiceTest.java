package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.find;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class FindTakenLectureServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindTakenLecturePort findTakenLecturePort;
	@InjectMocks
	private FindTakenLectureService findTakenLectureService;

	@DisplayName("해당 학생의 수강정보를 조회하고 정렬한다.")
	@Test
	void getTakenLectures() {
		//given
		User user = User.builder().id(1L).build();
		Lecture 채플 = createLecture(1L, "KMA02101", "채플", 0);
		Lecture 영어1 = createLecture(2L, "KMA02106", "영어1",2);
		Lecture 영어2 = createLecture(3L, "KMA02107", "영어2",2);
		Lecture 글쓰기 = createLecture(4L, "KMA02104", "글쓰기",3);
		Lecture 세계화와사회변화 = createLecture(5L, "KMA02113", "세계화와사회변화",3);
		Lecture 고전으로읽는인문학 = createLecture(6L, "KMA02130", "고전으로읽는인문학", 3);
		Lecture 사차산업혁명과미래사회진로선택 = createLecture(7L, "KMA02141", "4차산업혁명과미래사회진로선택", 2);

		Instant basicTime =  Instant.parse("2022-02-15T00:00:00.00Z");
		Instant customTime1 = Instant.parse("2022-12-15T00:00:00.00Z");
		Instant customTime2 = Instant.parse("2022-12-15T00:10:00.00Z");
		List<TakenLecture> takenLectures = new ArrayList<>(List.of(
			createTakenLecture(1L, user, 채플, 2020, Semester.FIRST, basicTime),
			createTakenLecture(2L, user, 채플, 2021, Semester.FIRST, basicTime),
			createTakenLecture(3L, user, 고전으로읽는인문학, 2020, Semester.SECOND, basicTime),
			createTakenLecture(4L, user, 글쓰기,2020, Semester.WINTER, basicTime),
			createTakenLecture(5L, user, 영어1, 2020, Semester.SUMMER, basicTime),
			createTakenLecture(6L, user, 세계화와사회변화, 2021, Semester.SECOND, basicTime),
			createTakenLecture(7L, user, 영어2, 2099, null, customTime1),
			createTakenLecture(8L, user, 사차산업혁명과미래사회진로선택, 2099, null, customTime2)
		));

		given(findUserUseCase.findUserById(anyLong())).willReturn(user);
		given(findTakenLecturePort.findTakenLecturesByUser(any(User.class))).willReturn(takenLectures);

		//when
		FindTakenLectureResponse response = findTakenLectureService.getTakenLectures(1L);

		//then
		assertThat(response.getTotalCredit()).isEqualTo(15);
		assertThat(response.getTakenLectures())
			.hasSize(8)
			.extracting("id", "year", "semester", "lectureCode", "lectureName", "credit")
			.containsExactly(
				tuple(8L, "CUSTOM", "CUSTOM", "KMA02141", "4차산업혁명과미래사회진로선택", 2),
				tuple(7L, "CUSTOM", "CUSTOM", "KMA02107", "영어2", 2),
				tuple(6L, "2021", "2학기", "KMA02113", "세계화와사회변화", 3),
				tuple(2L, "2021", "1학기", "KMA02101", "채플", 0),
				tuple(4L, "2020", "동계계절", "KMA02104", "글쓰기", 3),
				tuple(3L, "2020", "2학기", "KMA02130", "고전으로읽는인문학", 3),
				tuple(5L, "2020", "하계계절", "KMA02106", "영어1", 2),
				tuple(1L, "2020", "1학기", "KMA02101", "채플", 0)
			);
	}

	@DisplayName("채플을 4개 이상 수강했을 경우에 2점을 추가한다.")
	@Test
	void addTwoPointIfChapelCountIsOver4() {
		//given
		User user = User.builder().id(1L).build();
		Lecture 채플 = createLecture(1L, "KMA02101", "채플", 0);
		Lecture 영어1 = createLecture(2L, "KMA02106", "영어1",2);
		Lecture 영어2 = createLecture(3L, "KMA02107", "영어2",2);
		Lecture 글쓰기 = createLecture(4L, "KMA02104", "글쓰기",3);
		Lecture 세계화와사회변화 = createLecture(5L, "KMA02113", "세계화와사회변화",3);
		Lecture 고전으로읽는인문학 = createLecture(6L, "KMA02130", "고전으로읽는인문학", 3);
		Lecture 사차산업혁명과미래사회진로선택 = createLecture(7L, "KMA02141", "4차산업혁명과미래사회진로선택", 2);

		Instant basicTime =  Instant.parse("2022-02-15T00:00:00.00Z");
		Instant customTime1 = Instant.parse("2022-12-15T00:00:00.00Z");
		Instant customTime2 = Instant.parse("2022-12-15T00:10:00.00Z");
		List<TakenLecture> takenLectures = new ArrayList<>(List.of(
			createTakenLecture(1L, user, 채플, 2020, Semester.FIRST, basicTime),
			createTakenLecture(2L, user, 채플, 2021, Semester.FIRST, basicTime),
			createTakenLecture(3L, user, 고전으로읽는인문학, 2020, Semester.SECOND, basicTime),
			createTakenLecture(4L, user, 글쓰기,2020, Semester.WINTER, basicTime),
			createTakenLecture(5L, user, 영어1, 2020, Semester.SUMMER, basicTime),
			createTakenLecture(6L, user, 세계화와사회변화, 2021, Semester.SECOND, basicTime),
			createTakenLecture(7L, user, 영어2, 2099, null, customTime1),
			createTakenLecture(8L, user, 사차산업혁명과미래사회진로선택, 2099, null, customTime2),
			createTakenLecture(9L, user, 채플, 2021, Semester.SECOND, basicTime),
			createTakenLecture(10L, user, 채플, 2099, null, basicTime)
		));

		given(findUserUseCase.findUserById(anyLong())).willReturn(user);
		given(findTakenLecturePort.findTakenLecturesByUser(any(User.class))).willReturn(takenLectures);

		//when
		FindTakenLectureResponse response = findTakenLectureService.getTakenLectures(1L);

		//then
		assertThat(response.getTotalCredit()).isEqualTo(17);
		assertThat(response.getTakenLectures()).hasSize(10);
	}

	private TakenLecture createTakenLecture(Long id, User user, Lecture lecture, Integer year,
		Semester semester, Instant createdAt) {
		return TakenLecture.builder()
			.id(id)
			.user(user)
			.lecture(lecture)
			.year(year)
			.semester(semester)
			.createdAt(createdAt)
			.build();
	}

	private Lecture createLecture(Long id, String lectureCode, String name, int credit) {
		return Lecture.builder()
			.id(id)
			.lectureCode(lectureCode)
			.name(name)
			.isRevoked(0)
			.duplicateCode(null)
			.credit(credit)
			.build();
	}
}
