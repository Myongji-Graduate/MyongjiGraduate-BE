package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateGraduationServiceTest {

	@Mock
	private com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;
	@Mock
	private com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase findTakenLectureUseCase;
	@Mock
	private CalculateCommonCultureGraduationService calculateCommonCultureGraduationService;
	@Mock
	private CalculateCoreCultureGraduationService calculateCoreCultureGraduationService;
	@Mock
	private CalculateBasicAcademicalCultureGraduationService calculateBasicAcademicalCultureGraduationService;
	@Mock
	private CalculateMajorGraduationService calculateMajorGraduationService;
	@Mock
	private com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationUseCase updateStudentInformationUseCase;
	@Mock
	private com.plzgraduate.myongjigraduatebe.graduation.domain.service.StudentGraduationStrategyFactory strategyFactory;

	private CalculateGraduationService calculateGraduationService;

	@BeforeEach
	void setUp() {
		calculateGraduationService = new CalculateGraduationService(
			findBasicAcademicalCulturePort,
			findTakenLectureUseCase,
			calculateCommonCultureGraduationService,
			calculateCoreCultureGraduationService,
			calculateBasicAcademicalCultureGraduationService,
			calculateMajorGraduationService,
			updateStudentInformationUseCase,
			strategyFactory
		);
	}

	@DisplayName("overwriteTakenCreditWithActualEarnedCredits - 원본 수강 인벤토리와 편입/교환 학점을 합산하여 실제 취득학점을 업데이트한다.")
	@Test
	void overwriteTakenCreditWithActualEarnedCredits_updatesGraduationResultTakenCredit() {
		// given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(20)
			.studentCategory(StudentCategory.NORMAL)
			.transferCredit(TransferCredit.from("3/6/0/0")) // normalCulture=3, majorLecture=6
			.exchangeCredit(ExchangeCredit.from("0/0/0/0/0/0/0/0")) // all zero (8-field legacy)
			.build();

		// 원본 인벤토리: 3학점 강의 2개 = 6학점
		Set<TakenLecture> taken = Set.of(
			TakenLecture.builder().lecture(Lecture.builder().id("L001").credit(3).build()).build(),
			TakenLecture.builder().lecture(Lecture.builder().id("L002").credit(3).build()).build()
		);
		TakenLectureInventory originalInventory = TakenLectureInventory.from(taken);

		GraduationResult graduationResult = GraduationResult.create(
			ChapelResult.create(TakenLectureInventory.from(Set.of())),
			List.of()
		);

		// when
		calculateGraduationService.overwriteTakenCreditWithActualEarnedCredits(
			user, graduationResult, originalInventory
		);

		// then: 6(수강) + 3(편입 교양) + 6(편입 전공) + 0 + 0 + ... = 15
		assertThat(graduationResult.getTakenCredit()).isEqualTo(15.0);
	}

	@DisplayName("overwriteTakenCreditWithActualEarnedCredits - 교환학점이 있는 경우 모두 합산된다.")
	@Test
	void overwriteTakenCreditWithActualEarnedCredits_includesExchangeCredits() {
		// given
		User user = User.builder()
			.id(1L)
			.primaryMajor("경영학전공")
			.entryYear(20)
			.studentCategory(StudentCategory.NORMAL)
			.transferCredit(TransferCredit.from("0/0/0/0"))
			.exchangeCredit(ExchangeCredit.from("1/2/3/4/0/0/0/0/5")) // 9-field: all non-zero
			.build();

		TakenLectureInventory originalInventory = TakenLectureInventory.from(Set.of());
		GraduationResult graduationResult = GraduationResult.create(
			ChapelResult.create(TakenLectureInventory.from(Set.of())),
			List.of()
		);

		// when
		calculateGraduationService.overwriteTakenCreditWithActualEarnedCredits(
			user, graduationResult, originalInventory
		);

		// then: 0(수강) + 0(편입) + 1+2+3+4+0+0+0+0+5(교환) = 15
		assertThat(graduationResult.getTakenCredit()).isEqualTo(15.0);
	}

	@DisplayName("generateChapelResult - 채플 대체 학생은 ChapelResult.replaced()를 반환한다.")
	@Test
	void generateChapelResult_whenChapleReplaced_returnsReplaced() {
		// given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(20)
			.studentCategory(StudentCategory.NORMAL)
			.isChapleReplaced(true)
			.transferCredit(TransferCredit.from("0/0/0/0"))
			.exchangeCredit(ExchangeCredit.from("0/0/0/0/0/0/0/0"))
			.build();
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of());

		// when
		ChapelResult chapelResult = calculateGraduationService.generateChapelResult(user, inventory);

		// then
		assertThat(chapelResult.isCompleted()).isTrue();
	}

	@DisplayName("generateChapelResult - 일반 학생은 수강 인벤토리로 채플 결과를 생성한다.")
	@Test
	void generateChapelResult_whenNotReplaced_createFromInventory() {
		// given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(20)
			.studentCategory(StudentCategory.NORMAL)
			.isChapleReplaced(false)
			.transferCredit(TransferCredit.from("0/0/0/0"))
			.exchangeCredit(ExchangeCredit.from("0/0/0/0/0/0/0/0"))
			.build();
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of());

		// when
		ChapelResult chapelResult = calculateGraduationService.generateChapelResult(user, inventory);

		// then
		assertThat(chapelResult).isNotNull();
		assertThat(chapelResult.isCompleted()).isFalse();
	}
}
