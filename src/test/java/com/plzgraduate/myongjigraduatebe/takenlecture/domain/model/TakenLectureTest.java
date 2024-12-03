package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TakenLectureTest {

	private static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("수강과목은 과목, 수강 년도, 수강 학기 모두 같으면 같은 것으로 인정된다.")
	@Test
	void lectureEqualsTest() {
		//given
		User user = UserFixture.국제통상학과_19학번();
		TakenLecture takenLectureA = TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2019,
			Semester.FIRST);
		TakenLecture takenLectureB = TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2019,
			Semester.FIRST);

		//when
		boolean equalsResult = takenLectureA.equals(takenLectureB);
		Set<TakenLecture> takenLectureSet = new HashSet<>();
		takenLectureSet.add(takenLectureA);
		takenLectureSet.add(takenLectureB);

		//then
		assertThat(equalsResult).isTrue();
		assertThat(takenLectureSet).hasSize(1);
	}

	@DisplayName("수강과목은 과목, 수강 년도, 수강 학기 모두 같으면 같은 것으로 인정된다.")
	@Test
	void lectureUnEqualsTest() {
		//given
		User user = UserFixture.국제통상학과_19학번();
		TakenLecture takenLectureA = TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2019,
			Semester.FIRST);
		TakenLecture takenLectureB = TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2018,
			Semester.SECOND);
		TakenLecture takenLectureC = TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2019,
			Semester.SECOND);

		//when
		Set<TakenLecture> takenLectureSet = new HashSet<>();
		takenLectureSet.add(takenLectureA);
		takenLectureSet.add(takenLectureB);
		takenLectureSet.add(takenLectureC);

		//then
		assertThat(takenLectureSet).hasSize(3);
	}

}
