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

class TakenLectureInventoryTest {

	private final User user = UserFixture.경영학과_19학번_ENG34();
	private final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("수강과목 목록에서 교양 수강과목 목록을 반환한다.")
	@Test
	void getTakenCultureLectures() {
		//given //when
		TakenLectureInventory takenLectureInventory = getTakenLectureInventory();
		Set<TakenLecture> cultureLectures = takenLectureInventory.getCultureLectures();

		//then
		assertThat(cultureLectures).hasSize(takenLectureInventory.getTakenLectures()
			.size());
	}

	@DisplayName("수강과목 목록에서 처리 완료된 수강과목들을 제거한다.")
	@Test
	void handleFinishedTakenLectures() {
		//given
		TakenLectureInventory takenLectureInventory = getTakenLectureInventory();
		int beforeHandleSize = takenLectureInventory.getTakenLectures()
			.size();
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

	@DisplayName("총 수강학점을 계산한다.")
	@Test
	void calculateTotalCredit() {
		//given
		Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
			TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, Semester.FIRST), // credit 2
			TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, Semester.FIRST)  // credit 2
		));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		//when
		int calculatedCredit = takenLectureInventory.calculateTotalCredit();

		//then
		assertThat(calculatedCredit).isEqualTo(4);
	}

	@DisplayName("수강과목 목록에서 처리 완료된 과목을 제거한다.")
	@Test
	void sync() {
		//given
		TakenLectureInventory takenLectureInventory = getTakenLectureInventory();
		int beforeHandleSize = takenLectureInventory.getTakenLectures()
			.size();
		Set<Lecture> finishedLectures = new HashSet<>(Set.of(
			mockLectureMap.get("KMA00101"),
			mockLectureMap.get("KMA02102")
		));

		//when
		takenLectureInventory.sync(finishedLectures);

		//then
		assertThat(takenLectureInventory.getTakenLectures())
			.hasSize(beforeHandleSize - finishedLectures.size());
	}

	private TakenLectureInventory getTakenLectureInventory() {
		return TakenLectureInventory.from(new HashSet<>(Set.of(
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
	}
}
