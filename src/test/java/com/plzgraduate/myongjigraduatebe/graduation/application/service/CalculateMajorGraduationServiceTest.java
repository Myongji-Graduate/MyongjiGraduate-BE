package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.CORE_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_MANDATORY_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.SUB_MAJOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.BusinessCrossEnrollmentManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.ElectiveMajorManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MandatoryMajorManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.OptionalMandatoryMajorHandler;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.ReplaceMandatoryMajorHandler;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.submajor.SubMajorGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateMajorGraduationServiceTest {

	@Mock
	private FindMajorPort findMajorPort;

	private CalculateMajorGraduationService calculateMajorGraduationService;


	@BeforeEach
	void setUp() {
		MandatoryMajorManager mandatoryMajorManager = new MandatoryMajorManager(
			List.of(new OptionalMandatoryMajorHandler(), new ReplaceMandatoryMajorHandler()));
		ElectiveMajorManager electiveMajorManager = new ElectiveMajorManager();
		SubMajorGraduationManager subMajorGraduationManager = new SubMajorGraduationManager();
		MajorGraduationManager majorGraduationManager = new MajorGraduationManager(
			mandatoryMajorManager, electiveMajorManager);
		BusinessCrossEnrollmentManager businessCrossEnrollmentManager = new BusinessCrossEnrollmentManager(findMajorPort);
		calculateMajorGraduationService = new CalculateMajorGraduationService(findMajorPort,
			majorGraduationManager, subMajorGraduationManager, businessCrossEnrollmentManager
		);
	}

	@DisplayName("MAJOR 관련 카테고리 일때만 MajorGraduationService를 호출한다.")
	@Test
	void shouldSupportMajorCategory() {
		assertTrue(calculateMajorGraduationService.supports(PRIMARY_MANDATORY_MAJOR));
		assertTrue(calculateMajorGraduationService.supports(PRIMARY_ELECTIVE_MAJOR));
		assertTrue(calculateMajorGraduationService.supports(DUAL_MANDATORY_MAJOR));
		assertTrue(calculateMajorGraduationService.supports(DUAL_ELECTIVE_MAJOR));
		assertTrue(calculateMajorGraduationService.supports(SUB_MAJOR));
		assertFalse(calculateMajorGraduationService.supports(COMMON_CULTURE));
		assertFalse(calculateMajorGraduationService.supports(CORE_CULTURE));
		assertFalse(calculateMajorGraduationService.supports(PRIMARY_BASIC_ACADEMICAL_CULTURE));
		assertFalse(calculateMajorGraduationService.supports(DUAL_BASIC_ACADEMICAL_CULTURE));
	}

	@DisplayName("유저의 주전공필수 졸업결과를 계산한다.")
	@Test
	void shouldCalculateSingleDetailGraduationIfPrimaryMandatory() {
		//given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19)
			.build();
		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder()
					.id("HEC01211")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder()
					.id("HEC01204")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23)
			));
		given(findMajorPort.findMajor(user.getPrimaryMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01211") //전공 필수
						.credit(3)
						.build())
					.build(),
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01305") //전공 선택
						.credit(3)
						.build())
					.build()
			));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.primaryMajorCredit(70)
			.build();

		//when
		DetailGraduationResult detailPrimaryMandatoryMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, PRIMARY_MANDATORY_MAJOR, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailPrimaryMandatoryMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(PRIMARY_MANDATORY_MAJOR, false, 6, 3.0);
	}

	@DisplayName("유저의 주전공선택 상세 졸업결과를 계산한다.")
	@Test
	void calculateSingleDetailGraduationIfPrimaryElective() {
		//given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19)
			.build();
		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				// 전공 필수
				MajorLecture.of(Lecture.builder()
					.id("HEC01211")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23),
				// 전공 선택
				MajorLecture.of(Lecture.builder()
					.id("HEC01305")
					.credit(3)
					.build(), "응용소프트웨어전공", 0, 16, 23),
				// 전공 선택
				MajorLecture.of(Lecture.builder()
					.id("HEC01318")
					.credit(3)
					.build(), "응용소프트웨어전공", 0, 16, 23)
			));
		given(findMajorPort.findMajor(user.getPrimaryMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01211") //전공 필수
						.credit(3)
						.build())
					.build(),
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01305") //전공 선택
						.credit(3)
						.build())
					.build()
			));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.primaryMajorCredit(70)
			.build();

		//when
		DetailGraduationResult detailPrimaryElectiveMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, PRIMARY_ELECTIVE_MAJOR, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailPrimaryElectiveMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(PRIMARY_ELECTIVE_MAJOR, false, 67, 3.0);
	}

	@DisplayName("유저의 복수전공필수 졸업결과를 계산한다.")
	@Test
	void calculateSingleDetailGraduationIfDualMandatory() {
		//given
		User user = User.builder()
			.id(1L)
			.primaryMajor("")
			.dualMajor("응용소프트웨어전공")
			.entryYear(19)
			.build();
		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder()
					.id("HEC01211")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder()
					.id("HEC01204")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23)
			));
		given(findMajorPort.findMajor(user.getDualMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01211") //전공 필수
						.credit(3)
						.build())
					.build(),
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01305") //전공 선택
						.credit(3)
						.build())
					.build()
			));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.dualMajorCredit(70)
			.build();

		//when
		DetailGraduationResult detailDualMandatoryMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, DUAL_MANDATORY_MAJOR, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailDualMandatoryMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(DUAL_MANDATORY_MAJOR, false, 6, 3.0);
	}

	@DisplayName("유저의 복수전공선택 졸업결과를 계산한다.")
	@Test
	void calculateCoreCulture() {
		//given
		User user = User.builder()
			.id(1L)
			.primaryMajor("")
			.dualMajor("응용소프트웨어전공")
			.entryYear(19)
			.build();
		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder()
					.id("HEC01211")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder()
					.id("HEC01304")
					.credit(3)
					.build(), "응용소프트웨어전공", 0, 16, 23)
			));
		given(findMajorPort.findMajor(user.getDualMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01211") //전공 필수
						.credit(3)
						.build())
					.build(),
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01304") //전공 선택
						.credit(3)
						.build())
					.build()
			));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.dualMajorCredit(70)
			.build();

		//when
		DetailGraduationResult detailDualMandatoryMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, DUAL_ELECTIVE_MAJOR, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailDualMandatoryMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(DUAL_ELECTIVE_MAJOR, false, 67, 3.0);
	}

	@DisplayName("편입생 주전공필수 졸업결과를 계산한다.")
	@Test
	void calculateSingleDetailGraduationForTransferPrimaryMandatory() {
		// given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19)
			.studentCategory(StudentCategory.TRANSFER)
			.transferCredit(TransferCredit.from("0/15/0/0"))
			.build();

		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder()
					.id("HEC01211")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder()
					.id("HEC01204")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23)
			)
		);
		given(findMajorPort.findMajor(user.getPrimaryMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01211") // 전공 필수
						.credit(3)
						.build())
					.build()
			)
		);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.primaryMajorCredit(70)
			.build();

		// when
		DetailGraduationResult detailPrimaryMandatoryMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, PRIMARY_MANDATORY_MAJOR, takenLectureInventory, graduationRequirement);

		// then
		assertThat(detailPrimaryMandatoryMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(PRIMARY_MANDATORY_MAJOR, false, 6, 3.0);
	}

	@DisplayName("편입생 주전공선택 졸업결과를 계산한다.")
	@Test
	void calculateSingleDetailGraduationForTransferPrimaryElective() {
		// given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19)
			.studentCategory(StudentCategory.TRANSFER)
			.transferCredit(TransferCredit.from("0/15/0/0")) // 15 전공 선택 학점
			.build();

		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				// 전공 필수
				MajorLecture.of(Lecture.builder()
					.id("HEC01211")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23),
				// 전공 선택
				MajorLecture.of(Lecture.builder()
					.id("HEC01305")
					.credit(3)
					.build(), "응용소프트웨어전공", 0, 16, 23)
			)
		);
		given(findMajorPort.findMajor(user.getPrimaryMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01305") // 전공 선택
						.credit(3)
						.build())
					.build()
			)
		);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.primaryMajorCredit(70) // 전공 학점 총합
			.build();

		// when
		DetailGraduationResult detailPrimaryElectiveMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, PRIMARY_ELECTIVE_MAJOR, takenLectureInventory, graduationRequirement);

		// then
		assertThat(detailPrimaryElectiveMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(PRIMARY_ELECTIVE_MAJOR, 18.0); // 15 (편입 학점) + 3 (수강 학점)
	}

	@DisplayName("교환학생 전공선택 학점 졸업 결과를 계산한다.")
	@Test
	void calculateSingleDetailGraduationForExchangePrimaryElective() {
		// given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19)
			.studentCategory(StudentCategory.NORMAL)
			.exchangeCredit(ExchangeCredit.from("0/0/3/0/0/0/0/0")) // 3 전공 선택 학점
			.build();

		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder()
					.id("HEC01211")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder()
					.id("HEC01305")
					.credit(3)
					.build(), "응용소프트웨어전공", 0, 16, 23)
			)
		);
		given(findMajorPort.findMajor(user.getPrimaryMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01305") // 전공 선택
						.credit(3)
						.build())
					.build()
			)
		);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.primaryMajorCredit(70) // 전공 학점 총합
			.build();

		// when
		DetailGraduationResult detailPrimaryElectiveMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, PRIMARY_ELECTIVE_MAJOR, takenLectureInventory, graduationRequirement);

		// then
		assertThat(detailPrimaryElectiveMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(PRIMARY_ELECTIVE_MAJOR, false, 67, 6.0); // 3 (교환 학점) + 3 (교환학생 인정 학점)
	}

	@DisplayName("경영대학 내 복수전공자는 주전공에서 이수한 전공필수 과목이 복수전공필수에도 교차 인정된다.")
	@Test
	void applyBusinessCollegeCrossEnrollmentForDualMandatory() {
		// given
		// 경영정보학과 선택필수(OptionalMandatory.MANAGEMENT_INFORMATION) 중 비폐지 과목
		// 19학번부터 2개 선택 → 주전공에서 이미 소비된 경우 복수전공에서 못 찾는 문제를 복원으로 해결
		Lecture hrManagementLecture = Lecture.of("HBX01113", "hrManagementLecture", 3, 0, null);
		Lecture financeManagementLecture = Lecture.of("HBX01147", "financeManagementLecture", 3, 0, null);
		Lecture dualExclusiveMandatoryLecture = Lecture.of("HBX01401", "dualExclusiveMandatoryLecture", 3, 0, null); // 경영정보학과 전용

		// 방현우: 60190872, 국제통상학과 19학번, 경영정보학과 복수전공
		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"방현우", "60190872", 19,
			"국제통상학과", "경영정보학과", null,
			StudentCategory.DUAL_MAJOR,
			"0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		// 주전공(국제통상학과): hrManagementLecture + financeManagementLecture을 전공필수로 요구 (총 6학점)
		// → OptionalMandatoryMajorHandler가 "국제통상학과"에 미적용 (국제통상학전공만 적용)
		Set<MajorLecture> primaryLectures = new HashSet<>(Set.of(
			MajorLecture.of(hrManagementLecture, "국제통상학과", 1, 16, 24),
			MajorLecture.of(financeManagementLecture, "국제통상학과", 1, 16, 24)
		));
		given(findMajorPort.findMajor("국제통상학과")).willReturn(primaryLectures);

		// 복수전공(경영정보학과): 같은 과목 + dualExclusiveMandatoryLecture + 선택 10과목 (총 6+3+30=39학점)
		// OptionalMandatoryMajorHandler가 적용 → hrManagementLecture/financeManagementLecture 중 2개 이상 수강 확인
		Set<MajorLecture> dualLectures = new HashSet<>();
		dualLectures.add(MajorLecture.of(hrManagementLecture, "경영정보학과", 1, 16, 24));
		dualLectures.add(MajorLecture.of(financeManagementLecture, "경영정보학과", 1, 16, 24));
		dualLectures.add(MajorLecture.of(dualExclusiveMandatoryLecture, "경영정보학과", 1, 16, 24));
		for (int i = 1; i <= 10; i++) {
			dualLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX020%02d", i), "복전선택" + i, 3, 0, null),
				"경영정보학과", 0, 16, 24
			));
		}
		given(findMajorPort.findMajor("경영정보학과")).willReturn(dualLectures);

		// 수강: 공유 전공필수 2과목(6학점) + dualExclusiveMandatoryLecture(3학점) + 복전선택 10과목(30학점)
		// hrManagementLecture/financeManagementLecture은 주전공이 먼저 소비 → 복수전공 인벤토리에 없음
		Set<TakenLecture> takenLectures = new HashSet<>();
		takenLectures.add(TakenLecture.builder().lecture(hrManagementLecture).build());
		takenLectures.add(TakenLecture.builder().lecture(financeManagementLecture).build());
		takenLectures.add(TakenLecture.builder().lecture(dualExclusiveMandatoryLecture).build());
		for (int i = 1; i <= 10; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX020%02d", i), "복전선택" + i, 3, 0, null))
				.build());
		}
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		// 주전공 6학점(필수만), 복수전공 39학점(필수9 + 선택30)
		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(6)
			.dualMajorCredit(39)
			.build();

		// when
		List<DetailGraduationResult> results = calculateMajorGraduationService.calculateAllDetailGraduation(
			user, inventory, req);

		// then
		DetailGraduationResult dualMandatory = results.stream()
			.filter(r -> r.getGraduationCategory() == DUAL_MANDATORY_MAJOR)
			.findFirst().orElseThrow();
		DetailGraduationResult dualElective = results.stream()
			.filter(r -> r.getGraduationCategory() == DUAL_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();

		// 주전공 필수 이수 과목 복원 후 복수전공필수 완료
		assertThat(dualMandatory.isCompleted()).isTrue();
		assertThat(dualMandatory.getTakenCredit()).isEqualTo(9.0);
		// 복수전공선택도 완료
		assertThat(dualElective.isCompleted()).isTrue();
		assertThat(dualElective.getTakenCredit()).isEqualTo(30.0);
	}

	@DisplayName("경영대학 내 복수전공자는 주전공선택 초과학점이 최대 9학점까지 복수전공선택에 교차 인정된다.")
	@Test
	void applyBusinessCollegeCrossEnrollmentForDualElective() {
		// given
		// 경영정보학과 선택필수 과목 (19학번+, 2개 선택)
		Lecture hrManagementLecture = Lecture.of("HBX01113", "hrManagementLecture", 3, 0, null);
		Lecture financeManagementLecture = Lecture.of("HBX01147", "financeManagementLecture", 3, 0, null);
		Lecture dualExclusiveMandatoryLecture = Lecture.of("HBX01402", "dualExclusiveMandatoryLecture", 3, 0, null);

		// 주전공(국제통상학과): 선택필수 + 선택 12과목 → 선택 12과목 초과로 9학점 normalLeftCredit 발생
		Set<MajorLecture> primaryLectures = new HashSet<>();
		primaryLectures.add(MajorLecture.of(hrManagementLecture, "국제통상학과", 1, 16, 24));
		primaryLectures.add(MajorLecture.of(financeManagementLecture, "국제통상학과", 1, 16, 24));
		for (int i = 1; i <= 12; i++) {
			primaryLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX011%02d", i), "주전선택" + i, 3, 0, null),
				"국제통상학과", 0, 16, 24
			));
		}
		given(findMajorPort.findMajor("국제통상학과")).willReturn(primaryLectures);

		// 복수전공(경영정보학과): 선택필수 3과목(9학점) + 선택 9과목(27학점) 총 36학점
		// 학생이 선택 6과목만 수강 → 9학점 부족, 교차인정으로 완료
		Set<MajorLecture> dualLectures = new HashSet<>();
		dualLectures.add(MajorLecture.of(hrManagementLecture, "경영정보학과", 1, 16, 24));
		dualLectures.add(MajorLecture.of(financeManagementLecture, "경영정보학과", 1, 16, 24));
		dualLectures.add(MajorLecture.of(dualExclusiveMandatoryLecture, "경영정보학과", 1, 16, 24));
		for (int i = 1; i <= 9; i++) {
			dualLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX020%02d", i), "복전선택" + i, 3, 0, null),
				"경영정보학과", 0, 16, 24
			));
		}
		given(findMajorPort.findMajor("경영정보학과")).willReturn(dualLectures);

		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"방현우", "60190872", 19,
			"국제통상학과", "경영정보학과", null,
			StudentCategory.DUAL_MAJOR,
			"0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		// 수강: 선택필수2과목(6) + 주전선택12과목(36) + dualExclusiveMandatoryLecture(3) + 복전선택6과목(18)
		Set<TakenLecture> takenLectures = new HashSet<>();
		takenLectures.add(TakenLecture.builder().lecture(hrManagementLecture).build());
		takenLectures.add(TakenLecture.builder().lecture(financeManagementLecture).build());
		for (int i = 1; i <= 12; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX011%02d", i), "주전선택" + i, 3, 0, null))
				.build());
		}
		takenLectures.add(TakenLecture.builder().lecture(dualExclusiveMandatoryLecture).build());
		for (int i = 1; i <= 6; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX020%02d", i), "복전선택" + i, 3, 0, null))
				.build());
		}
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		// 주전공 33학점(필수6+선택27), 복수전공 36학점(필수9+선택27)
		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(33)
			.dualMajorCredit(36)
			.build();

		// when
		List<DetailGraduationResult> results = calculateMajorGraduationService.calculateAllDetailGraduation(
			user, inventory, req);

		// then
		DetailGraduationResult dualElective = results.stream()
			.filter(r -> r.getGraduationCategory() == DUAL_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();

		// 복전선택 18학점 + 교차인정 9학점 = 27학점 → 완료
		assertThat(dualElective.isCompleted()).isTrue();
	}

	@DisplayName("경영대학 복수전공자의 주전공선택/복수전공선택이 각 3학점 부족할 때(27/27) 교차인정으로 둘 다 충족된다.")
	@Test
	void businessDualMajorCrossEnrollment_shortfall3Each_bothCompleted() {
		// 경영정보학과 OptionalMandatory 후보 과목 (chooseNumber=2, HBX01113+HBX01147 선택)
		Lecture hrManagementLecture = Lecture.of("HBX01113", "hrManagementLecture", 3, 0, null);
		Lecture financeManagementLecture = Lecture.of("HBX01147", "financeManagementLecture", 3, 0, null);

		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"테스트", "60190001", 19,
			"국제통상학과", "경영정보학과", null,
			StudentCategory.DUAL_MAJOR, "0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		// 주전공(국제통상학과): 필수 2과목(6학점) + 선택 10과목(30학점 요건) = 총 36학점
		Set<MajorLecture> primaryLectures = new HashSet<>();
		for (int i = 1; i <= 2; i++) {
			primaryLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX001%02d", i), "주전필수" + i, 3, 0, null),
				"국제통상학과", 1, 16, 24));
		}
		for (int i = 1; i <= 10; i++) {
			primaryLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX002%02d", i), "주전선택" + i, 3, 0, null),
				"국제통상학과", 0, 16, 24));
		}
		given(findMajorPort.findMajor("국제통상학과")).willReturn(primaryLectures);

		// 복수전공(경영정보학과): 선택필수 2과목(6학점) + 선택 10과목(30학점 요건) = 총 36학점
		Set<MajorLecture> dualLectures = new HashSet<>();
		dualLectures.add(MajorLecture.of(hrManagementLecture, "경영정보학과", 1, 16, 24));
		dualLectures.add(MajorLecture.of(financeManagementLecture, "경영정보학과", 1, 16, 24));
		for (int i = 1; i <= 10; i++) {
			dualLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX003%02d", i), "복전선택" + i, 3, 0, null),
				"경영정보학과", 0, 16, 24));
		}
		given(findMajorPort.findMajor("경영정보학과")).willReturn(dualLectures);

		// 수강: 주전필수2(6학점) + 주전선택9(27학점) + 복전필수2(6학점) + 복전선택9(27학점)
		Set<TakenLecture> takenLectures = new HashSet<>();
		for (int i = 1; i <= 2; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX001%02d", i), "주전필수" + i, 3, 0, null)).build());
		}
		for (int i = 1; i <= 9; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX002%02d", i), "주전선택" + i, 3, 0, null)).build());
		}
		takenLectures.add(TakenLecture.builder().lecture(hrManagementLecture).build());
		takenLectures.add(TakenLecture.builder().lecture(financeManagementLecture).build());
		for (int i = 1; i <= 9; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX003%02d", i), "복전선택" + i, 3, 0, null)).build());
		}
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		// 주전공 36학점(필수6+선택30), 복수전공 36학점(필수6+선택30)
		// 주전공선택 27/30(3부족), 복수전공선택 27/30(3부족) → toDual=3, toPrimary=3 → 둘 다 30/30
		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(36)
			.dualMajorCredit(36)
			.build();

		List<DetailGraduationResult> results = calculateMajorGraduationService.calculateAllDetailGraduation(
			user, inventory, req);

		DetailGraduationResult primaryElective = results.stream()
			.filter(r -> r.getGraduationCategory() == PRIMARY_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();
		DetailGraduationResult dualElective = results.stream()
			.filter(r -> r.getGraduationCategory() == DUAL_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();

		// primaryElectiveMajorDetailGraduationResult.isCompleted는 isolation 시점(교차인정 전) 기준으로 고정됨
		// 교차인정 후 실제 완료 여부는 내부 DetailCategoryResult에서 확인
		assertThat(primaryElective.getDetailCategory().getFirst().isCompleted()).isTrue();
		assertThat(dualElective.isCompleted()).isTrue();
	}

	@DisplayName("경영대학 복수전공자의 주전공선택/복수전공선택이 각 9학점 부족할 때(21/21) 복수전공선택만 충족되고 주전공선택은 미충족된다.")
	@Test
	void businessDualMajorCrossEnrollment_shortfall9Each_onlyDualCompleted() {
		Lecture hrManagementLecture = Lecture.of("HBX01113", "hrManagementLecture", 3, 0, null);
		Lecture financeManagementLecture = Lecture.of("HBX01147", "financeManagementLecture", 3, 0, null);

		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"테스트", "60190002", 19,
			"국제통상학과", "경영정보학과", null,
			StudentCategory.DUAL_MAJOR, "0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		Set<MajorLecture> primaryLectures = new HashSet<>();
		for (int i = 1; i <= 2; i++) {
			primaryLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX001%02d", i), "주전필수" + i, 3, 0, null),
				"국제통상학과", 1, 16, 24));
		}
		for (int i = 1; i <= 10; i++) {
			primaryLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX002%02d", i), "주전선택" + i, 3, 0, null),
				"국제통상학과", 0, 16, 24));
		}
		given(findMajorPort.findMajor("국제통상학과")).willReturn(primaryLectures);

		Set<MajorLecture> dualLectures = new HashSet<>();
		dualLectures.add(MajorLecture.of(hrManagementLecture, "경영정보학과", 1, 16, 24));
		dualLectures.add(MajorLecture.of(financeManagementLecture, "경영정보학과", 1, 16, 24));
		for (int i = 1; i <= 10; i++) {
			dualLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX003%02d", i), "복전선택" + i, 3, 0, null),
				"경영정보학과", 0, 16, 24));
		}
		given(findMajorPort.findMajor("경영정보학과")).willReturn(dualLectures);

		// 수강: 주전필수2(6학점) + 주전선택7(21학점) + 복전필수2(6학점) + 복전선택7(21학점)
		Set<TakenLecture> takenLectures = new HashSet<>();
		for (int i = 1; i <= 2; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX001%02d", i), "주전필수" + i, 3, 0, null)).build());
		}
		for (int i = 1; i <= 7; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX002%02d", i), "주전선택" + i, 3, 0, null)).build());
		}
		takenLectures.add(TakenLecture.builder().lecture(hrManagementLecture).build());
		takenLectures.add(TakenLecture.builder().lecture(financeManagementLecture).build());
		for (int i = 1; i <= 7; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX003%02d", i), "복전선택" + i, 3, 0, null)).build());
		}
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		// 주전공선택 21/30(9부족), 복수전공선택 21/30(9부족) → toDual=9, toPrimary=0 → 복수전공만 30/30
		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(36)
			.dualMajorCredit(36)
			.build();

		List<DetailGraduationResult> results = calculateMajorGraduationService.calculateAllDetailGraduation(
			user, inventory, req);

		DetailGraduationResult primaryElective = results.stream()
			.filter(r -> r.getGraduationCategory() == PRIMARY_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();
		DetailGraduationResult dualElective = results.stream()
			.filter(r -> r.getGraduationCategory() == DUAL_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();

		assertThat(primaryElective.getDetailCategory().getFirst().isCompleted()).isFalse();
		assertThat(dualElective.isCompleted()).isTrue();
	}

	@DisplayName("경영대학 단일전공자가 타 경영대 전공 과목을 수강하면 최대 9학점이 주전공선택으로 인정된다.")
	@Test
	void businessSingleMajorCrossEnrollment_crossCreditsAppliedToElective() {
		Lecture hrManagementLecture = Lecture.of("HBX01113", "hrManagementLecture", 3, 0, null);
		Lecture financeManagementLecture = Lecture.of("HBX01147", "financeManagementLecture", 3, 0, null);

		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"테스트", "60190003", 19,
			"경영정보학과", null, null,
			StudentCategory.NORMAL, "0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		// 주전공(경영정보학과): 선택필수2과목(6학점) + 선택5과목(15학점 요건) = 총 21학점
		Set<MajorLecture> primaryLectures = new HashSet<>();
		primaryLectures.add(MajorLecture.of(hrManagementLecture, "경영정보학과", 1, 16, 24));
		primaryLectures.add(MajorLecture.of(financeManagementLecture, "경영정보학과", 1, 16, 24));
		for (int i = 1; i <= 5; i++) {
			primaryLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX004%02d", i), "managementInfoElectiveLecture" + i, 3, 0, null),
				"경영정보학과", 0, 16, 24));
		}
		given(findMajorPort.findMajor("경영정보학과")).willReturn(primaryLectures);

		// 교차수강 대상: 경영학전공 과목 3개(9학점)
		Set<MajorLecture> businessMajorLectures = new HashSet<>();
		for (int i = 1; i <= 3; i++) {
			businessMajorLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX005%02d", i), "경영학선택" + i, 3, 0, null),
				"경영학전공", 0, 16, 24));
		}
		given(findMajorPort.findMajor("경영학전공")).willReturn(businessMajorLectures);
		given(findMajorPort.findMajor("국제통상학과")).willReturn(new HashSet<>());

		// 수강: 선택필수2(6학점) + managementInfoElectiveLecture2(6학점) + 경영학전공과목3(9학점)
		// 주전공 처리 후 인벤토리에 경영학전공 9학점 잔류 → 교차인정 적용
		Set<TakenLecture> takenLectures = new HashSet<>();
		takenLectures.add(TakenLecture.builder().lecture(hrManagementLecture).build());
		takenLectures.add(TakenLecture.builder().lecture(financeManagementLecture).build());
		for (int i = 1; i <= 2; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX004%02d", i), "managementInfoElectiveLecture" + i, 3, 0, null)).build());
		}
		for (int i = 1; i <= 3; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX005%02d", i), "경영학선택" + i, 3, 0, null)).build());
		}
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		// 교차수강 전: 주전공선택 6/15(미충족) → 교차인정 +9학점 → 15/15(충족)
		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(21)
			.build();

		List<DetailGraduationResult> results = calculateMajorGraduationService.calculateAllDetailGraduation(
			user, inventory, req);

		DetailGraduationResult primaryMandatory = results.stream()
			.filter(r -> r.getGraduationCategory() == PRIMARY_MANDATORY_MAJOR)
			.findFirst().orElseThrow();
		DetailGraduationResult primaryElective = results.stream()
			.filter(r -> r.getGraduationCategory() == PRIMARY_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();

		assertThat(results).hasSize(2);
		assertThat(primaryMandatory.getTakenCredit()).isEqualTo(6.0);
		assertThat(primaryElective.isCompleted()).isTrue();
		assertThat(primaryElective.getTakenCredit()).isEqualTo(15.0);
		assertThat(primaryElective.getDetailCategory()).hasSize(1);
		assertThat(primaryElective.getDetailCategory().getFirst().getTakenLectures())
			.extracting(Lecture::getId)
			.contains("HBX00501", "HBX00502", "HBX00503");
	}

	@DisplayName("주전공이 경영대이고 복수전공이 비경영대인 학생은 타 경영대 전공 과목 9학점을 주전공선택으로 인정받는다.")
	@Test
	void businessPrimaryWithNonBusinessDual_crossCreditsAppliedToPrimaryElective() {
		Lecture hrManagementLecture = Lecture.of("HBX01113", "hrManagementLecture", 3, 0, null);
		Lecture financeManagementLecture = Lecture.of("HBX01147", "financeManagementLecture", 3, 0, null);

		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"테스트", "60190004", 19,
			"경영정보학과", "응용소프트웨어전공", null,
			StudentCategory.DUAL_MAJOR, "0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		Set<MajorLecture> primaryLectures = new HashSet<>();
		primaryLectures.add(MajorLecture.of(hrManagementLecture, "경영정보학과", 1, 16, 24));
		primaryLectures.add(MajorLecture.of(financeManagementLecture, "경영정보학과", 1, 16, 24));
		for (int i = 1; i <= 5; i++) {
			primaryLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX004%02d", i), "managementInfoElectiveLecture" + i, 3, 0, null),
				"경영정보학과", 0, 16, 24
			));
		}
		given(findMajorPort.findMajor("경영정보학과")).willReturn(primaryLectures);

		Set<MajorLecture> businessMajorLectures = new HashSet<>();
		for (int i = 1; i <= 3; i++) {
			businessMajorLectures.add(MajorLecture.of(
				Lecture.of(String.format("HBX005%02d", i), "경영학선택" + i, 3, 0, null),
				"경영학전공", 0, 16, 24
			));
		}
		given(findMajorPort.findMajor("경영학전공")).willReturn(businessMajorLectures);
		given(findMajorPort.findMajor("국제통상학과")).willReturn(new HashSet<>());

		Set<MajorLecture> dualLectures = new HashSet<>(Set.of(
			MajorLecture.of(Lecture.of("HEC01304", "객체지향프로그래밍", 3, 0, null), "응용소프트웨어전공", 0, 16, 24)
		));
		given(findMajorPort.findMajor("응용소프트웨어전공")).willReturn(dualLectures);

		Set<TakenLecture> takenLectures = new HashSet<>();
		takenLectures.add(TakenLecture.builder().lecture(hrManagementLecture).build());
		takenLectures.add(TakenLecture.builder().lecture(financeManagementLecture).build());
		for (int i = 1; i <= 2; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX004%02d", i), "managementInfoElectiveLecture" + i, 3, 0, null)).build());
		}
		for (int i = 1; i <= 3; i++) {
			takenLectures.add(TakenLecture.builder()
				.lecture(Lecture.of(String.format("HBX005%02d", i), "경영학선택" + i, 3, 0, null)).build());
		}
		takenLectures.add(TakenLecture.builder()
			.lecture(Lecture.of("HEC01304", "객체지향프로그래밍", 3, 0, null)).build());
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(21)
			.dualMajorCredit(3)
			.build();

		List<DetailGraduationResult> results = calculateMajorGraduationService.calculateAllDetailGraduation(
			user, inventory, req);

		DetailGraduationResult primaryElective = results.stream()
			.filter(r -> r.getGraduationCategory() == PRIMARY_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();
		DetailGraduationResult dualElective = results.stream()
			.filter(r -> r.getGraduationCategory() == DUAL_ELECTIVE_MAJOR)
			.findFirst().orElseThrow();

		assertThat(primaryElective.isCompleted()).isTrue();
		assertThat(primaryElective.getTakenCredit()).isEqualTo(15.0);
		assertThat(primaryElective.getDetailCategory().getFirst().getTakenLectures())
			.extracting(Lecture::getId)
			.contains("HBX00501", "HBX00502", "HBX00503");
		assertThat(dualElective.getTakenCredit()).isEqualTo(3.0);
	}

	@DisplayName("경영대학 복수전공자의 공통 필수 과목이 주전공에서 먼저 소비되어도 복수전공필수로 다시 인정된다.")
	@Test
	void businessDualMajor_commonMandatoryConsumedByPrimary_restoredForDualMandatory() {
		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"테스트", "60190872", 19,
			"국제통상학과", "경영정보학과", null,
			StudentCategory.DUAL_MAJOR, "0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		Lecture accountingPrinciplesLecture = Lecture.of("HBX01104", "accountingPrinciplesLecture", 3, 0, null);
		Lecture financeManagementIntroLecture = Lecture.of("HBX01105", "financeManagementIntroLecture", 3, 1, null);
		Lecture marketingIntroLecture = Lecture.of("HBX01106", "marketingIntroLecture", 3, 0, null);
		Lecture operationsManagementLecture = Lecture.of("HBX01143", "operationsManagementLecture", 3, 0, null);
		Lecture hrManagementLecture = Lecture.of("HBX01113", "hrManagementLecture", 3, 0, null);
		Lecture globalBusinessStrategyLecture = Lecture.of("HBX01129", "globalBusinessStrategyLecture", 3, 0, null);
		Lecture programmingBasicsLecture = Lecture.of("HBX01124", "programmingBasicsLecture", 3, 0, null);
		Lecture managementInfoElectiveLecture = Lecture.of("HCB03505", "모바일앱프로그래밍", 3, 0, null);

		Set<MajorLecture> primaryLectures = new HashSet<>(Set.of(
			MajorLecture.of(accountingPrinciplesLecture, "국제통상학과", 1, 16, 24),
			MajorLecture.of(financeManagementIntroLecture, "국제통상학과", 1, 16, 24),
			MajorLecture.of(marketingIntroLecture, "국제통상학과", 1, 16, 24),
			MajorLecture.of(operationsManagementLecture, "국제통상학과", 1, 16, 24),
			MajorLecture.of(hrManagementLecture, "국제통상학과", 1, 16, 24),
			MajorLecture.of(globalBusinessStrategyLecture, "국제통상학과", 1, 16, 24)
		));
		given(findMajorPort.findMajor("국제통상학과")).willReturn(primaryLectures);

		Set<MajorLecture> dualLectures = new HashSet<>(Set.of(
			MajorLecture.of(accountingPrinciplesLecture, "경영정보학과", 1, 16, 24),
			MajorLecture.of(financeManagementIntroLecture, "경영정보학과", 1, 16, 24),
			MajorLecture.of(marketingIntroLecture, "경영정보학과", 1, 16, 24),
			MajorLecture.of(operationsManagementLecture, "경영정보학과", 1, 16, 24),
			MajorLecture.of(programmingBasicsLecture, "경영정보학과", 1, 19, 24),
			MajorLecture.of(managementInfoElectiveLecture, "경영정보학과", 0, 16, 24)
		));
		given(findMajorPort.findMajor("경영정보학과")).willReturn(dualLectures);

		Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
			TakenLecture.builder().lecture(accountingPrinciplesLecture).build(),
			TakenLecture.builder().lecture(financeManagementIntroLecture).build(),
			TakenLecture.builder().lecture(marketingIntroLecture).build(),
			TakenLecture.builder().lecture(operationsManagementLecture).build(),
			TakenLecture.builder().lecture(hrManagementLecture).build(),
			TakenLecture.builder().lecture(globalBusinessStrategyLecture).build(),
			TakenLecture.builder().lecture(programmingBasicsLecture).build(),
			TakenLecture.builder().lecture(managementInfoElectiveLecture).build()
		));

		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(21)
			.dualMajorCredit(18)
			.build();

		List<DetailGraduationResult> results = calculateMajorGraduationService.calculateAllDetailGraduation(
			user, inventory, req);

		DetailGraduationResult dualMandatory = results.stream()
			.filter(r -> r.getGraduationCategory() == DUAL_MANDATORY_MAJOR)
			.findFirst().orElseThrow();

		assertThat(dualMandatory.isCompleted()).isTrue();
		assertThat(dualMandatory.getTakenCredit()).isEqualTo(15);
		assertThat(dualMandatory.getDetailCategory().getFirst().getTakenLectures())
			.extracting(Lecture::getId)
			.contains("HBX01104", "HBX01105", "HBX01106", "HBX01143", "HBX01124");
	}

	@DisplayName("교환 학생 유저의 복수전공선택 졸업결과를 계산한다.")
	@Test
	void calculateDualMajorWithExchangeCredits() {
		//given
		User user = User.builder()
			.id(1L)
			.studentCategory(StudentCategory.DUAL_MAJOR)
			.primaryMajor("")
			.dualMajor("응용소프트웨어전공")
			.entryYear(19)
			.exchangeCredit(ExchangeCredit.from("0/0/0/0/15/0/0/0")) // 교환 학점 15 설정
			.build();

		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder()
					.id("HEC01211")
					.credit(3)
					.build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder()
					.id("HEC01304")
					.credit(3)
					.build(), "응용소프트웨어전공", 0, 16, 23)
			));

		given(findMajorPort.findMajor(user.getDualMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01211") // 전공 필수
						.credit(3)
						.build())
					.build(),
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("HEC01304") // 전공 선택
						.credit(3)
						.build())
					.build()
			));

		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.dualMajorCredit(70)
			.build();

		//when
		DetailGraduationResult detailDualElectiveMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user,
			DUAL_ELECTIVE_MAJOR,
			takenLectureInventory,
			graduationRequirement
		);

		int additionalCredits = user.getExchangeCredit().getDualMajor();
		detailDualElectiveMajorGraduationResult.addCredit(additionalCredits);

		//then
		assertThat(detailDualElectiveMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(DUAL_ELECTIVE_MAJOR, false, 67, 18.0); // 교환학생 인점 학점 15 + 전공 선택 수강 학점 3 = 18
	}

	@DisplayName("부전공 카테고리는 SubMajorGraduationManager를 통해 계산한다.")
	@Test
	void calculateSingleDetailGraduationForSubMajor() {
		// given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.subMajor("데이터테크놀로지전공")
			.entryYear(19)
			.studentCategory(StudentCategory.SUB_MAJOR)
			.build();

		HashSet<MajorLecture> subMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder().id("HED01201").credit(3).build(),
					"데이터테크놀로지전공", 0, 16, 24),
				MajorLecture.of(Lecture.builder().id("HED01202").credit(3).build(),
					"데이터테크놀로지전공", 0, 16, 24)
			)
		);
		given(findMajorPort.findMajor(user.getSubMajor())).willReturn(subMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder().id("HED01201").credit(3).build())
					.build()
			)
		);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.subMajorCredit(21)
			.build();

		// when
		DetailGraduationResult result = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, SUB_MAJOR, takenLectureInventory, graduationRequirement
		);

		// then
		assertThat(result.getGraduationCategory()).isEqualTo(SUB_MAJOR);
		assertThat(result.getTakenCredit()).isEqualTo(3.0);
	}

	@DisplayName("경영대 복수전공 학생이 calculateSingleDetailGraduation 호출 시 교차인정 후 해당 카테고리 결과를 반환한다.")
	@Test
	void calculateSingleDetailGraduationForBusinessDualMajor() {
		// given: 경영학전공(주) + 국제통상학과(복수), 19학번, DUAL_MAJOR
		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"테스트", "60190010", 19,
			"경영학전공", "국제통상학과", null,
			StudentCategory.DUAL_MAJOR, "0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		Lecture primaryMandatory = Lecture.of("HBX01001", "주전필수", 3, 0, null);
		Lecture primaryElective = Lecture.of("HBX01100", "주전선택", 3, 0, null);
		Lecture dualMandatory = Lecture.of("HBX02001", "복전필수", 3, 0, null);
		Lecture dualElective = Lecture.of("HBX02100", "복전선택", 3, 0, null);

		Set<MajorLecture> primaryLectures = new HashSet<>(Set.of(
			MajorLecture.of(primaryMandatory, "경영학전공", 1, 16, 24),
			MajorLecture.of(primaryElective, "경영학전공", 0, 16, 24)
		));
		given(findMajorPort.findMajor("경영학전공")).willReturn(primaryLectures);

		Set<MajorLecture> dualLectures = new HashSet<>(Set.of(
			MajorLecture.of(dualMandatory, "국제통상학과", 1, 16, 24),
			MajorLecture.of(dualElective, "국제통상학과", 0, 16, 24)
		));
		given(findMajorPort.findMajor("국제통상학과")).willReturn(dualLectures);

		Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
			TakenLecture.builder().lecture(primaryMandatory).build(),
			TakenLecture.builder().lecture(primaryElective).build(),
			TakenLecture.builder().lecture(dualMandatory).build(),
			TakenLecture.builder().lecture(dualElective).build()
		));
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(6)
			.dualMajorCredit(6)
			.build();

		// when: PRIMARY_MANDATORY_MAJOR 카테고리 요청
		DetailGraduationResult result = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, PRIMARY_MANDATORY_MAJOR, inventory, req
		);

		// then
		assertThat(result.getGraduationCategory()).isEqualTo(PRIMARY_MANDATORY_MAJOR);
	}

	@DisplayName("경영대 단일전공 학생의 주전공선택 calculateSingleDetailGraduation 호출 시 교차인정이 적용된다.")
	@Test
	void calculateSingleDetailGraduationForBusinessSingleMajorElective() {
		// given: 경영정보학과 NORMAL, 19학번
		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"테스트", "60190020", 19,
			"경영정보학과", null, null,
			StudentCategory.NORMAL, "0/0/0/0", "0/0/0/0/0/0/0/0"
		);

		Lecture mandatory = Lecture.of("HBX01113", "hrManagementLecture", 3, 0, null);
		Lecture elective = Lecture.of("HBX01200", "managementInfoElectiveLecture", 3, 0, null);
		Lecture crossLecture = Lecture.of("HBX02200", "경영학선택", 3, 0, null);

		Set<MajorLecture> primaryLectures = new HashSet<>(Set.of(
			MajorLecture.of(mandatory, "경영정보학과", 1, 16, 24),
			MajorLecture.of(elective, "경영정보학과", 0, 16, 24)
		));
		given(findMajorPort.findMajor("경영정보학과")).willReturn(primaryLectures);
		given(findMajorPort.findMajor("경영학전공")).willReturn(
			Set.of(MajorLecture.of(crossLecture, "경영학전공", 0, 16, 24))
		);
		given(findMajorPort.findMajor("국제통상학과")).willReturn(Set.of());

		// 교차인정 대상 강의(crossLecture)를 수강
		Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
			TakenLecture.builder().lecture(mandatory).build(),
			TakenLecture.builder().lecture(crossLecture).build()
		));
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(9)
			.build();

		// when
		DetailGraduationResult result = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, PRIMARY_ELECTIVE_MAJOR, inventory, req
		);

		// then: 교차인정 강의 포함된 결과
		assertThat(result.getGraduationCategory()).isEqualTo(PRIMARY_ELECTIVE_MAJOR);
		assertThat(result.getTakenCredit()).isGreaterThanOrEqualTo(3.0);
	}

	@DisplayName("부전공 학생의 calculateAllDetailGraduation은 교환학점을 포함한 부전공 결과를 반환한다.")
	@Test
	void calculateAllDetailGraduationForSubMajor() {
		// given
		User user = UserFixture.createUser(
			"mj19", "1234", EnglishLevel.ENG34, KoreanLevel.FREE,
			"테스트", "60190030", 19,
			"응용소프트웨어전공", null, "데이터테크놀로지전공",
			StudentCategory.SUB_MAJOR, "0/0/0/0", "0/0/0/0/0/0/3/0" // subMajor 교환학점 3
		);

		Lecture primaryLecture = Lecture.of("HEC01211", "주전필수", 3, 0, null);
		Lecture primaryElective = Lecture.of("HEC01305", "주전선택", 3, 0, null);
		Set<MajorLecture> primaryLectures = new HashSet<>(Set.of(
			MajorLecture.of(primaryLecture, "응용소프트웨어전공", 1, 16, 24),
			MajorLecture.of(primaryElective, "응용소프트웨어전공", 0, 16, 24)
		));
		given(findMajorPort.findMajor("응용소프트웨어전공")).willReturn(primaryLectures);

		Lecture subLecture = Lecture.of("HED01201", "부전공선택", 3, 0, null);
		Set<MajorLecture> subMajorLectures = new HashSet<>(Set.of(
			MajorLecture.of(subLecture, "데이터테크놀로지전공", 0, 16, 24)
		));
		given(findMajorPort.findMajor("데이터테크놀로지전공")).willReturn(subMajorLectures);

		Set<TakenLecture> takenLectures = new HashSet<>(Set.of(
			TakenLecture.builder().lecture(primaryLecture).build(),
			TakenLecture.builder().lecture(subLecture).build()
		));
		TakenLectureInventory inventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement req = GraduationRequirement.builder()
			.primaryMajorCredit(6)
			.subMajorCredit(21)
			.build();

		// when
		List<DetailGraduationResult> results = calculateMajorGraduationService.calculateAllDetailGraduation(
			user, inventory, req
		);

		// then: 부전공 결과 포함
		DetailGraduationResult subMajorResult = results.stream()
			.filter(r -> r.getGraduationCategory() == SUB_MAJOR)
			.findFirst().orElseThrow();

		// 교환학점 3 포함 → takenCredit >= 3
		assertThat(subMajorResult.getTakenCredit()).isGreaterThanOrEqualTo(3.0);
	}
}
