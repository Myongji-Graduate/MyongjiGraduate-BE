package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.OptionalMandatoryPolicyFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy.CandidateLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("N개 중에 M개 이상을 수강할 경우 세부조건을 달성한다.")
class OptionalMandatoryMajorHandlerTest {

	private static final User user = UserFixture.경영학과_19학번_ENG12();
	private static final MajorType MAJOR_TYPE = MajorType.PRIMARY;
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
		MandatoryMajorSpecialCaseHandler exceptionHandler = OptionalMandatoryPolicyFixture.handler();
		MandatorySpecialCaseInformation mandatorySpecialCaseInformation = exceptionHandler.evaluate(
			user, MAJOR_TYPE, takenLectureInventory, mandatoryLectures, electiveLectures,
			OptionalMandatoryPolicyFixture.policies(user.getPrimaryMajor(), user.getEntryYear()))
			.orElseThrow();
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
		MandatoryMajorSpecialCaseHandler exceptionHandler = OptionalMandatoryPolicyFixture.handler();
		MandatorySpecialCaseInformation mandatorySpecialCaseInformation = exceptionHandler.evaluate(
			user, MAJOR_TYPE, takenLectureInventory, mandatoryLectures, electiveLectures,
			OptionalMandatoryPolicyFixture.policies(user.getPrimaryMajor(), user.getEntryYear()))
			.orElseThrow();
		boolean isCompleteMandatorySpecialCase = mandatorySpecialCaseInformation.isCompleteMandatorySpecialCase();
		int removedMandatoryTotalCredit = mandatorySpecialCaseInformation.getRemovedMandatoryTotalCredit();

		//then
		assertThat(isCompleteMandatorySpecialCase).isFalse();
		assertThat(removedMandatoryTotalCredit).isEqualTo(6);
		assertThat(mandatoryLectures).hasSize(6);
		assertThat(electiveLectures).isEmpty();
	}

	@DisplayName("구·신 과목을 모두 수강해도 하나의 선택지로 계산한다.")
	@Test
	void 동등과목_중복제거() {
		Lecture oldLecture = Lecture.of("OLD", "구과목", 3, 1, "OLD");
		Lecture newLecture = Lecture.of("NEW", "신과목", 3, 0, "OLD");
		Lecture another = Lecture.of("OTHER", "다른과목", 3, 0, null);
		OptionalMandatoryPolicy policy = OptionalMandatoryPolicy.builder()
			.name("테스트 선택필수")
			.major(user.getPrimaryMajor())
			.requiredCount(2)
			.requiredCredit(6)
			.candidateLectures(List.of(
				new CandidateLecture(oldLecture, "REPLACEMENT"),
				new CandidateLecture(newLecture, "REPLACEMENT"),
				new CandidateLecture(another, "OTHER")))
			.build();
		OptionalMandatoryMajorHandler handler = new OptionalMandatoryMajorHandler();
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
			TakenLecture.of(user, oldLecture, 2020, Semester.FIRST),
			TakenLecture.of(user, newLecture, 2021, Semester.FIRST)));

		MandatorySpecialCaseInformation result = handler.evaluate(
			user, MAJOR_TYPE, inventory,
			new HashSet<>(Set.of(oldLecture, newLecture, another)), new HashSet<>(),
			List.of(policy)).orElseThrow();

		assertThat(result.isCompleteMandatorySpecialCase()).isFalse();
	}

	@DisplayName("동등 과목을 모두 수강하면 한 과목만 전공필수로 인정한다.")
	@Test
	void 동등과목_필수학점_중복제거() {
		Lecture oldLecture = Lecture.of("OLD", "구과목", 3, 1, "OLD");
		Lecture newLecture = Lecture.of("NEW", "신과목", 3, 0, "OLD");
		OptionalMandatoryPolicy policy = OptionalMandatoryPolicy.builder()
			.name("테스트 선택필수")
			.major(user.getPrimaryMajor())
			.requiredCount(1)
			.requiredCredit(3)
			.candidateLectures(List.of(
				new CandidateLecture(oldLecture, "REPLACEMENT"),
				new CandidateLecture(newLecture, "REPLACEMENT")))
			.build();
		OptionalMandatoryMajorHandler handler = new OptionalMandatoryMajorHandler();
		MandatoryMajorManager manager = new MandatoryMajorManager(List.of(handler));
		Set<Lecture> mandatoryLectures = new HashSet<>(Set.of(oldLecture, newLecture));
		Set<Lecture> electiveLectures = new HashSet<>();
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
			TakenLecture.of(user, oldLecture, 2020, Semester.FIRST),
			TakenLecture.of(user, newLecture, 2021, Semester.FIRST)));

		DetailCategoryResult result = manager.createDetailCategoryResult(
			user, inventory, mandatoryLectures, electiveLectures, MAJOR_TYPE, List.of(policy));

		assertThat(result.isSatisfiedMandatory()).isTrue();
		assertThat(result.getTotalCredits()).isEqualTo(3);
		assertThat(result.getTakenCredits()).isEqualTo(3);
		assertThat(result.getTakenLectures()).containsExactly(oldLecture);
		assertThat(mandatoryLectures).containsExactly(oldLecture);
		assertThat(electiveLectures).containsExactly(newLecture);
		assertThat(inventory.getTakenLectures())
			.extracting(TakenLecture::getLecture)
			.containsExactly(newLecture);
	}

}
