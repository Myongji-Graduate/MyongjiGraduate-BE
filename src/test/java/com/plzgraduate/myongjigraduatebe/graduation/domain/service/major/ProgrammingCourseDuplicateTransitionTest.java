package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType.PRIMARY;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProgrammingCourseDuplicateTransitionTest {

	private static final String MAJOR = "응용소프트웨어전공";
	private static final Lecture PROCEDURAL = lecture(
		"HEB01101", "절차적사고와프로그래밍", 1, "HEB01101");
	private static final Lecture BASIC_PROGRAMMING_1 = lecture(
		"HEF01101", "기초프로그래밍1", 0, "HEB01101");
	private static final Lecture OBJECT_ORIENTED = lecture(
		"HEB01103", "객체지향적사고와프로그래밍", 1, "HEB01103");
	private static final Lecture OLD_BASIC_PROGRAMMING_2 = lecture(
		"HEB01105", "기초프로그래밍2", 1, "HEB01103");
	private static final Lecture BASIC_PROGRAMMING_2 = lecture(
		"HEF01102", "기초프로그래밍2", 0, "HEB01103");
	private static final Lecture OLD_DATABASE = lecture(
		"HEC01204", "DB설계및구현1", 1, "HEB01207");
	private static final Lecture DATABASE = lecture(
		"HEB01207", "데이터베이스", 0, "HEB01207");

	private final MajorGraduationManager manager = new MajorGraduationManager(
		new MandatoryMajorManager(List.of()),
		new ElectiveMajorManager());
	private final User user = UserFixture.응용소프트웨어전공_19학번();

	@DisplayName("2025-2까지 객체지향과 기초프로그래밍2를 모두 이수하면 각각 전공학점으로 인정한다.")
	@Test
	void recognizesCrossCurriculumCoursesTakenBefore2026Separately() {
		DetailCategoryResult result = calculateElectiveResult(Set.of(
			TakenLecture.of(user, OBJECT_ORIENTED, 2020, Semester.SECOND),
			TakenLecture.of(user, BASIC_PROGRAMMING_2, 2025, Semester.SECOND)
		));

		assertThat(result.getTakenCredits()).isEqualTo(6);
		assertThat(result.getTakenLectures())
			.containsExactlyInAnyOrder(OBJECT_ORIENTED, BASIC_PROGRAMMING_2);
	}

	@DisplayName("2025-2까지 절차적사고와 기초프로그래밍1을 모두 이수하면 각각 전공학점으로 인정한다.")
	@Test
	void recognizesProceduralAndBasicProgrammingOneSeparatelyBefore2026() {
		DetailCategoryResult result = calculateElectiveResult(Set.of(
			TakenLecture.of(user, PROCEDURAL, 2024, Semester.SECOND),
			TakenLecture.of(user, BASIC_PROGRAMMING_1, 2025, Semester.FIRST)
		));

		assertThat(result.getTakenCredits()).isEqualTo(6);
		assertThat(result.getTakenLectures())
			.containsExactlyInAnyOrder(PROCEDURAL, BASIC_PROGRAMMING_1);
	}

	@DisplayName("2026-1 이후 기초프로그래밍2를 추가 이수하면 객체지향과 중복으로 처리한다.")
	@Test
	void treatsCourseTakenFrom2026AsDuplicate() {
		DetailCategoryResult result = calculateElectiveResult(Set.of(
			TakenLecture.of(user, OBJECT_ORIENTED, 2020, Semester.SECOND),
			TakenLecture.of(user, BASIC_PROGRAMMING_2, 2026, Semester.FIRST)
		));

		assertThat(result.getTakenCredits()).isEqualTo(3);
		assertThat(result.getTakenLectures()).containsExactly(OBJECT_ORIENTED);
	}

	@DisplayName("구·신 기초프로그래밍2 코드는 시행 전 수강이어도 동일과목으로 한 번만 인정한다.")
	@Test
	void keepsExistingReplacementCourseDeduplication() {
		DetailCategoryResult result = calculateElectiveResult(Set.of(
			TakenLecture.of(user, OLD_BASIC_PROGRAMMING_2, 2024, Semester.SECOND),
			TakenLecture.of(user, BASIC_PROGRAMMING_2, 2025, Semester.SECOND)
		));

		assertThat(result.getTakenCredits()).isEqualTo(3);
		assertThat(result.getTakenLectures()).containsExactly(OLD_BASIC_PROGRAMMING_2);
	}

	@DisplayName("응용소프트웨어 구 학번이 이수한 새 기초프로그래밍2는 전공선택으로 인정한다.")
	@Test
	void recognizesNewBasicProgrammingTwoAsAppliedSoftwareElective() {
		DetailGraduationResult result = manager.createDetailGraduationResult(
			user,
			PRIMARY,
			TakenLectureInventory.from(Set.of(
				TakenLecture.of(user, BASIC_PROGRAMMING_2, 2025, Semester.SECOND)
			)),
			new HashSet<>(Set.of(major(BASIC_PROGRAMMING_2, 0, 16, 99))),
			30,
			List.of());

		assertThat(result.getDetailCategory().get(0).getTakenLectures()).isEmpty();
		assertThat(result.getDetailCategory().get(1).getTakenLectures())
			.containsExactly(BASIC_PROGRAMMING_2);
	}

	@DisplayName("데이터테크놀로지 구 학번이 이수한 새 기초프로그래밍2는 전공선택으로 인정한다.")
	@Test
	void recognizesNewBasicProgrammingTwoAsLegacyDataTechnologyElective() {
		User dataTechnologyUser = UserFixture.데이터테크놀로지전공_19학번();
		DetailGraduationResult result = manager.createDetailGraduationResult(
			dataTechnologyUser,
			PRIMARY,
			TakenLectureInventory.from(Set.of(
				TakenLecture.of(
					dataTechnologyUser, BASIC_PROGRAMMING_2, 2025, Semester.SECOND)
			)),
			new HashSet<>(Set.of(major(BASIC_PROGRAMMING_2, 0, 16, 24))),
			30,
			List.of());

		assertThat(result.getDetailCategory().get(0).getTakenLectures()).isEmpty();
		assertThat(result.getDetailCategory().get(1).getTakenLectures())
			.containsExactly(BASIC_PROGRAMMING_2);
	}

	@DisplayName("폐강 구과목 수강 이력은 같은 학과·학번의 활성 대체 전필로 인정한다.")
	@Test
	void recognizesRevokedTakenLectureByActiveMandatoryReplacementPolicy() {
		DetailGraduationResult result = manager.createDetailGraduationResult(
			user,
			PRIMARY,
			TakenLectureInventory.from(Set.of(
				TakenLecture.of(user, OLD_DATABASE, 2020, Semester.FIRST)
			)),
			new HashSet<>(Set.of(major(DATABASE, 1, 16, 24))),
			30,
			List.of());

		DetailCategoryResult mandatory = result.getDetailCategory().get(0);
		assertThat(mandatory.getTotalCredits()).isEqualTo(3);
		assertThat(mandatory.getTakenCredits()).isEqualTo(3);
		assertThat(mandatory.getTakenLectures()).containsExactly(OLD_DATABASE);
		assertThat(mandatory.getHaveToLectures()).isEmpty();
	}

	@DisplayName("대체 활성 과목이 전필이 아니면 폐강 구과목도 전필로 인정하지 않는다.")
	@Test
	void doesNotRecognizeRevokedTakenLectureAsMandatoryWhenReplacementIsElective() {
		DetailGraduationResult result = manager.createDetailGraduationResult(
			user,
			PRIMARY,
			TakenLectureInventory.from(Set.of(
				TakenLecture.of(user, OLD_DATABASE, 2020, Semester.FIRST)
			)),
			new HashSet<>(Set.of(major(DATABASE, 0, 16, 24))),
			30,
			List.of());

		assertThat(result.getDetailCategory().get(0).getTakenLectures()).isEmpty();
		assertThat(result.getDetailCategory().get(1).getTakenLectures()).containsExactly(OLD_DATABASE);
	}

	@DisplayName("폐강 과목에 남은 옛 전필 행은 활성 대체 과목이 전선이면 무시한다.")
	@Test
	void ignoresLegacyMandatoryMappingOfRevokedLecture() {
		DetailGraduationResult result = manager.createDetailGraduationResult(
			user,
			PRIMARY,
			TakenLectureInventory.from(Set.of(
				TakenLecture.of(user, OLD_DATABASE, 2020, Semester.FIRST)
			)),
			new HashSet<>(Set.of(
				major(OLD_DATABASE, 1, 16, 24),
				major(DATABASE, 0, 16, 24)
			)),
			30,
			List.of());

		assertThat(result.getDetailCategory().get(0).getTakenLectures()).isEmpty();
		assertThat(result.getDetailCategory().get(1).getTakenLectures()).containsExactly(OLD_DATABASE);
	}

	private DetailCategoryResult calculateElectiveResult(Set<TakenLecture> takenLectures) {
		Set<MajorLecture> majorLectures = new HashSet<>(Set.of(
			major(PROCEDURAL),
			major(BASIC_PROGRAMMING_1),
			major(OBJECT_ORIENTED),
			major(OLD_BASIC_PROGRAMMING_2),
			major(BASIC_PROGRAMMING_2)
		));
		DetailGraduationResult result = manager.createDetailGraduationResult(
			user,
			PRIMARY,
			TakenLectureInventory.from(takenLectures),
			majorLectures,
			30,
			List.of());
		return result.getDetailCategory().get(1);
	}

	private static MajorLecture major(Lecture lecture) {
		return major(lecture, 0, 16, 99);
	}

	private static MajorLecture major(
		Lecture lecture,
		int mandatory,
		int startEntryYear,
		int endEntryYear
	) {
		return MajorLecture.of(lecture, MAJOR, mandatory, startEntryYear, endEntryYear);
	}

	private static Lecture lecture(
		String id,
		String name,
		int isRevoked,
		String duplicateCode
	) {
		return Lecture.of(id, name, 3, isRevoked, duplicateCode);
	}
}
