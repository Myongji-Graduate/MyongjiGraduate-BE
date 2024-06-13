package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import static org.assertj.core.api.Assertions.assertThat;

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

@DisplayName("N개 중에 M개 이상을 수강할 경우 세부조건을 달성한다.")
class OptionalMandatoryMandatoryMajorHandlerTest {

	private static final User user = UserFixture.경영학과_19학번_ENG12();
	private static final MajorGraduationCategory majorGraduationCategory = MajorGraduationCategory.PRIMARY;
	private static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("3개 중에 1개를 수강할 경우 세부조건을 달성한다.")
	@Test
	void 세개중한과목_수강() {

		//given
		Set<Lecture> mandatoryLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HBX01128"), //국제통상원론
			mockLectureMap.get("HBX01127"), //국제경영학
			mockLectureMap.get("HBY01103"), //경영정보(구)
			mockLectureMap.get("HBX01125"), //경영정보(신)
			mockLectureMap.get("HBX01105"), //재무관리원론
			mockLectureMap.get("HBX01113") //인적자원관리
		));
		Set<Lecture> electiveLectures = new HashSet<>();
		Set<TakenLecture> takenLectures = Set.of(
			TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2020, Semester.FIRST), //국제통상원론
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원론
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2020, Semester.SECOND) //인적자원관리
		);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		//when
		MandatoryMajorSpecialCaseHandler exceptionHandler = new OptionalMandatoryMandatoryMajorHandler();
		MandatorySpecialCaseInformation mandatorySpecialCaseInformation = exceptionHandler.getMandatorySpecialCaseInformation(
			user, majorGraduationCategory, takenLectureInventory, mandatoryLectures, electiveLectures);
		boolean isCompleteMandatorySpecialCase = mandatorySpecialCaseInformation.isCompleteMandatorySpecialCase();
		int removedMandatoryTotalCredit = mandatorySpecialCaseInformation.getRemovedMandatoryTotalCredit();

		//then
		assertThat(isCompleteMandatorySpecialCase).isTrue();
		assertThat(removedMandatoryTotalCredit).isZero();
		assertThat(mandatoryLectures).hasSize(3);
		assertThat(electiveLectures).hasSize(3);
	}

	@DisplayName("3개 중에 0개를 수강할 경우 세부조건을 달성하지 못한다.")
	@Test
	void 전공선택과목_미수강() {

		//given
		Set<Lecture> mandatoryLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HBX01128"), //국제통상원론
			mockLectureMap.get("HBX01127"), //국제경영학
			mockLectureMap.get("HBY01103"), //경영정보(구)
			mockLectureMap.get("HBX01125"), //경영정보(신)
			mockLectureMap.get("HBX01105"), //재무관리원론
			mockLectureMap.get("HBX01113") //인적자원관리
		));
		Set<Lecture> electiveLectures = new HashSet<>();
		Set<TakenLecture> takenLectures = Set.of(
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원론
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2020, Semester.SECOND) //인적자원관리
		);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		//when
		MandatoryMajorSpecialCaseHandler exceptionHandler = new OptionalMandatoryMandatoryMajorHandler();
		MandatorySpecialCaseInformation mandatorySpecialCaseInformation = exceptionHandler.getMandatorySpecialCaseInformation(
			user, majorGraduationCategory, takenLectureInventory, mandatoryLectures, electiveLectures);
		boolean isCompleteMandatorySpecialCase = mandatorySpecialCaseInformation.isCompleteMandatorySpecialCase();
		int removedMandatoryTotalCredit = mandatorySpecialCaseInformation.getRemovedMandatoryTotalCredit();

		//then
		assertThat(isCompleteMandatorySpecialCase).isFalse();
		assertThat(removedMandatoryTotalCredit).isEqualTo(6);
		assertThat(mandatoryLectures).hasSize(6);
		assertThat(electiveLectures).isEmpty();
	}

}
