package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindTakenLectureServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindTakenLecturePort findTakenLecturePort;
	@InjectMocks
	private FindTakenLectureService findTakenLectureService;

	@DisplayName("해당 학생의 수강정보를 조회한다.")
	@Test
	void findTakenLectures() {
		//given
		User user = User.builder()
			.id(1L)
			.build();
		Lecture 채플 = createLecture("KMA02101", "채플", 0);
		Lecture 영어1 = createLecture("KMA02106", "영어1", 2);
		Lecture 영어2 = createLecture("KMA02107", "영어2", 2);
		Lecture 글쓰기 = createLecture("KMA02104", "글쓰기", 3);
		Lecture 세계화와사회변화 = createLecture("KMA02113", "세계화와사회변화", 3);
		Lecture 고전으로읽는인문학 = createLecture("KMA02130", "고전으로읽는인문학", 3);
		Lecture 사차산업혁명과미래사회진로선택 = createLecture("KMA02141", "4차산업혁명과미래사회진로선택", 2);

		Instant basicTime = Instant.parse("2022-02-15T00:00:00.00Z");
		Instant customTime2 = Instant.parse("2022-12-15T00:10:00.00Z");
		Instant customTime1 = Instant.parse("2022-12-15T00:00:00.00Z");
		List<TakenLecture> takenLectures = new ArrayList<>(List.of(
			createTakenLecture(1L, user, 채플, 2020, Semester.FIRST, basicTime),
			createTakenLecture(2L, user, 채플, 2021, Semester.FIRST, basicTime),
			createTakenLecture(4L, user, 글쓰기, 2020, Semester.WINTER, basicTime),
			createTakenLecture(3L, user, 고전으로읽는인문학, 2020, Semester.SECOND, basicTime),
			createTakenLecture(5L, user, 영어1, 2020, Semester.SUMMER, basicTime),
			createTakenLecture(6L, user, 세계화와사회변화, 2021, Semester.SECOND, basicTime),
			createTakenLecture(7L, user, 영어2, 2099, null, customTime1),
			createTakenLecture(8L, user, 사차산업혁명과미래사회진로선택, 2099, null, customTime2)
		));

		given(findUserUseCase.findUserById(anyLong())).willReturn(user);
		given(findTakenLecturePort.findTakenLecturesByUser(any(User.class))).willReturn(
			takenLectures);

		//when
		TakenLectureInventory foundTakenLectures = findTakenLectureService.findTakenLectures(1L);

		//then
		assertThat(foundTakenLectures.calculateTotalCredit()).isEqualTo(15);
		assertThat(foundTakenLectures.getTakenLectures())
			.hasSize(8)
			.extracting("id", "lecture")
			.contains(
				tuple(8L, 사차산업혁명과미래사회진로선택),
				tuple(7L, 영어2),
				tuple(6L, 세계화와사회변화),
				tuple(2L, 채플),
				tuple(4L, 글쓰기),
				tuple(3L, 고전으로읽는인문학),
				tuple(5L, 영어1),
				tuple(1L, 채플)
			);
	}

	@DisplayName("채플을 4개 이상 수강했을 경우에 2점을 추가한다.")
	@Test
	void addTwoPointIfChapelCountIsOver4() {
		//given
		User user = User.builder()
			.id(1L)
			.build();
		Lecture 채플 = createLecture("KMA02101", "채플", 0);
		Lecture 영어1 = createLecture("KMA02106", "영어1", 2);
		Lecture 영어2 = createLecture("KMA02107", "영어2", 2);
		Lecture 글쓰기 = createLecture("KMA02104", "글쓰기", 3);
		Lecture 세계화와사회변화 = createLecture("KMA02113", "세계화와사회변화", 3);
		Lecture 고전으로읽는인문학 = createLecture("KMA02130", "고전으로읽는인문학", 3);
		Lecture 사차산업혁명과미래사회진로선택 = createLecture("KMA02141", "4차산업혁명과미래사회진로선택", 2);

		Instant basicTime = Instant.parse("2022-02-15T00:00:00.00Z");
		Instant customTime1 = Instant.parse("2022-12-15T00:00:00.00Z");
		Instant customTime2 = Instant.parse("2022-12-15T00:10:00.00Z");
		List<TakenLecture> takenLectures = new ArrayList<>(List.of(
			createTakenLecture(1L, user, 채플, 2020, Semester.FIRST, basicTime),
			createTakenLecture(2L, user, 채플, 2021, Semester.FIRST, basicTime),
			createTakenLecture(3L, user, 고전으로읽는인문학, 2020, Semester.SECOND, basicTime),
			createTakenLecture(4L, user, 글쓰기, 2020, Semester.WINTER, basicTime),
			createTakenLecture(5L, user, 영어1, 2020, Semester.SUMMER, basicTime),
			createTakenLecture(6L, user, 세계화와사회변화, 2021, Semester.SECOND, basicTime),
			createTakenLecture(7L, user, 영어2, 2099, null, customTime1),
			createTakenLecture(8L, user, 사차산업혁명과미래사회진로선택, 2099, null, customTime2),
			createTakenLecture(9L, user, 채플, 2021, Semester.SECOND, basicTime),
			createTakenLecture(10L, user, 채플, 2099, null, basicTime)
		));

		given(findUserUseCase.findUserById(anyLong())).willReturn(user);
		given(findTakenLecturePort.findTakenLecturesByUser(any(User.class))).willReturn(
			takenLectures);

		//when
		TakenLectureInventory foundTakenLectures = findTakenLectureService.findTakenLectures(1L);

		//then
		assertThat(foundTakenLectures.calculateTotalCredit()).isEqualTo(17);
		assertThat(foundTakenLectures.getTakenLectures()).hasSize(10);
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

	private Lecture createLecture(String lectureCode, String name, int credit) {
		return Lecture.builder()
			.id(lectureCode)
			.name(name)
			.isRevoked(0)
			.duplicateCode(null)
			.credit(credit)
			.build();
	}
}
