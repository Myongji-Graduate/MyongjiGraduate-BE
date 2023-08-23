package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

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
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@DisplayName("21학번 이전 철학과 학생의 경우 폐지된 전공필수의 대체 과목을 인정한다.")
class ReplaceMandatoryMajorHandlerTest {
	private static final User user = UserFixture.철학과_20학번();
	private static final StudentInformation studentInformation = user.getStudentInformation();
	private static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("답사1와 답사2를 수강했을 경우 세부조건을 달성한다.")
	@Test
	void 답사과목_수강() {
		//given
		Set<Lecture> mandatoryLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HAI01110"), //답사1
			mockLectureMap.get("HAI01111"), //답사2
			mockLectureMap.get("HAI01112"), //논리학
			mockLectureMap.get("HAI01566") //서양철학개론
		));
		Set<Lecture> electiveLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HAI01348"), //신유학의이해
			mockLectureMap.get("HAI01247") //유학사상의이해
		));
		Set<TakenLecture> takenLectures = Set.of(
			TakenLecture.of(user, mockLectureMap.get("HAI01110"), 2020, Semester.FIRST), //답사1
			TakenLecture.of(user, mockLectureMap.get("HAI01111"), 2020, Semester.SECOND), //답사2
			TakenLecture.of(user, mockLectureMap.get("HAI01348"), 2020, Semester.SECOND) //신유학의이해
		);
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);

		//when
		MajorExceptionHandler exceptionHandler = new ReplaceMandatoryMajorHandler();
		boolean checkMandatoryCondition = exceptionHandler.checkMandatoryCondition(studentInformation,
			takenLectureInventory, mandatoryLectures, electiveLectures);
		int removedMandatoryTotalCredit = exceptionHandler.getRemovedMandatoryTotalCredit();
		//then
		assertThat(checkMandatoryCondition).isTrue();
		assertThat(removedMandatoryTotalCredit).isZero();
		assertThat(mandatoryLectures).hasSize(4);
		assertThat(electiveLectures).hasSize(2);
	}

	@DisplayName("답사1,답사2를 수강했을 못했고, 대체과목을 수강했을 경우 세부조건을 달성한다. 이때 나중에 들은 대체과목은 전공선택으로 인정된다.")
	@Test
	void 답사과목_미수강_대체과목_수강() {
		//given
		Set<Lecture> mandatoryLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HAI01110"), //답사1
			mockLectureMap.get("HAI01111"), //답사2
			mockLectureMap.get("HAI01112"), //논리학
			mockLectureMap.get("HAI01566") //서양철학개론
		));
		Set<Lecture> electiveLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HAI01348"), //신유학의이해
			mockLectureMap.get("HAI01247") //유학사상의이해
		));
		Set<TakenLecture> takenLectures = Set.of(
			TakenLecture.of(user, mockLectureMap.get("HAI01348"), 2021, Semester.FIRST), //신유학의이해
			TakenLecture.of(user, mockLectureMap.get("HAI01247"), 2021, Semester.SECOND) //유학사상의이해
		);
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);

		//when
		MajorExceptionHandler exceptionHandler = new ReplaceMandatoryMajorHandler();
		boolean checkMandatoryCondition = exceptionHandler.checkMandatoryCondition(studentInformation,
			takenLectureInventory, mandatoryLectures, electiveLectures);
		int removedMandatoryTotalCredit = exceptionHandler.getRemovedMandatoryTotalCredit();
		//then
		assertThat(checkMandatoryCondition).isTrue();
		assertThat(removedMandatoryTotalCredit).isZero();
		assertThat(mandatoryLectures).hasSize(5).contains(mockLectureMap.get("HAI01348"));
		assertThat(electiveLectures).hasSize(1).contains(mockLectureMap.get("HAI01247"));
	}

	@DisplayName("답사1,답사2를 수강했을 못했고, 대체과목을 수강하지 못했을 경우 대체과목은 전공필수 과목으로 이동한다.")
	@Test
	void 답사_미수강_대체과목_미수강() {
		//given
		Set<Lecture> mandatoryLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HAI01110"), //답사1
			mockLectureMap.get("HAI01111"), //답사2
			mockLectureMap.get("HAI01112"), //논리학
			mockLectureMap.get("HAI01566") //서양철학개론
		));
		Set<Lecture> electiveLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HAI01348"), //신유학의이해
			mockLectureMap.get("HAI01247") //유학사상의이해
		));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(new HashSet<>());

		//when
		MajorExceptionHandler exceptionHandler = new ReplaceMandatoryMajorHandler();
		boolean checkMandatoryCondition = exceptionHandler.checkMandatoryCondition(studentInformation,
			takenLectureInventory, mandatoryLectures, electiveLectures);
		int removedMandatoryTotalCredit = exceptionHandler.getRemovedMandatoryTotalCredit();
		//then
		assertThat(checkMandatoryCondition).isFalse();
		assertThat(removedMandatoryTotalCredit).isEqualTo(3);
		assertThat(mandatoryLectures).hasSize(6);
		assertThat(electiveLectures).isEmpty();

	}
}
