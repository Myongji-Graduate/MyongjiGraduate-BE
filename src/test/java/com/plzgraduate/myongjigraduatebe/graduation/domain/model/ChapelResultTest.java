package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class ChapelResultTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("채플 수강 과목이 4개 이상일 경우 채플 졸업 결과는 이수 완료이다.")
	@Test
	void completedChapelResult() {
		//given
		User user = UserFixture.경영학과_19학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2019, Semester.FIRST), //채플
			TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2019, Semester.SECOND),
			TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2020, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2020, Semester.SECOND)
		)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);
		ChapelResult chapelResult = ChapelResult.create(takenLectureInventory);

		//when
		chapelResult.checkCompleted();

		//then
		assertThat(chapelResult.isCompleted()).isTrue();
	}

	@DisplayName("채플 수강 과목이 4개 미만일 경우 채플 졸업 결과는 미이수 이다.")
	@Test
	void unCompletedChapelResult() {
	    //given
		User user = UserFixture.경영학과_19학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2019, Semester.FIRST), //채플
			TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2019, Semester.SECOND),
			TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2020, Semester.SECOND)
		)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);
		ChapelResult chapelResult = ChapelResult.create(takenLectureInventory);

	    //when
		chapelResult.checkCompleted();

	    //then
		assertThat(chapelResult.isCompleted()).isFalse();
	}

}
