package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class TakenLectureInventoryTest {

	private final User user = UserFixture.경영학과_19학번_ENG34();
	private final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
	private final TakenLectureInventory takenLectureInventory = new TakenLectureInventory(new HashSet<>(Set.of(
		TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02106"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02107"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02123"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02124"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02108"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02109"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02125"), 2023, Semester.FIRST),
		TakenLecture.of(user, mockLectureMap.get("KMA02126"), 2023, Semester.FIRST)
	)));

	@DisplayName("수강과목 목록에서 교양 수강과목 목록을 반환한다.")
	@Test
	void getTakenCultureLectures() {
		//given //when
		Set<TakenLecture> cultureLectures = takenLectureInventory.getCultureLectures();

		//then
		assertThat(cultureLectures).hasSize(takenLectureInventory.getTakenLectures().size());
	}

	@DisplayName("수강과목 목록에서 처리 완료된 과목을 제거한다.")
	@Test
	void handleFinishedTakenLectures() {
		//given
		int beforeHandleSize = takenLectureInventory.getTakenLectures().size();
		Set<TakenLecture> finishedTakenLecture = new HashSet<>(Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02122"), 2019, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02141"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02106"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02107"), 2023, Semester.FIRST),
			TakenLecture.of(user, mockLectureMap.get("KMA02126"), 2023, Semester.FIRST)
		));

		//when
		takenLectureInventory.handleFinishedTakenLectures(finishedTakenLecture);

		//then
		assertThat(takenLectureInventory.getTakenLectures())
			.hasSize(beforeHandleSize - finishedTakenLecture.size());
	}

}
