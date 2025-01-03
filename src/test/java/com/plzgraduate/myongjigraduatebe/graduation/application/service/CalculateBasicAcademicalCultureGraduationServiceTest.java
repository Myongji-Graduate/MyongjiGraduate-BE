package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.CORE_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.BUSINESS;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.ICT;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.DUAL_MAJOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BasicAcademicalGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BusinessBasicAcademicalGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.DefaultBasicAcademicalGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.SocialScienceBasicAcademicGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
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
class CalculateBasicAcademicalCultureGraduationServiceTest {

	@Mock
	private FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;

	private CalculateBasicAcademicalCultureGraduationService calculateBasicAcademicalCultureGraduationService;

	private User user;
	private User user2;

	@BeforeEach
	void setUp() {
		List<BasicAcademicalGraduationManager> basicAcademicalGraduationManagers =
			List.of(new DefaultBasicAcademicalGraduationManager(),
				new BusinessBasicAcademicalGraduationManager(),
				new SocialScienceBasicAcademicGraduationManager());
		calculateBasicAcademicalCultureGraduationService = new CalculateBasicAcademicalCultureGraduationService(
			findBasicAcademicalCulturePort, basicAcademicalGraduationManagers);
		user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.dualMajor("경영학과")
			.studentCategory(DUAL_MAJOR)
			.entryYear(19)
			.build();

		 user2 = User.builder()
				.id(1L)
				.primaryMajor("응용소프트웨어전공")
				.dualMajor("경영학과")
				.studentCategory(DUAL_MAJOR)
				 .exchangeCredit(ExchangeCredit.from("0/0/0/6/0/0/0/0"))
				.entryYear(19)
				.build();
	}

	@DisplayName("BASIC_ACADEMICAL_CULTURE 관련 카테고리 일때만 BasicAcademicalCultureGraduationService를 호출한다.")
	@Test
	void shouldSupportBasicAcademicalCultureCategory() {
		assertTrue(calculateBasicAcademicalCultureGraduationService.supports(
			PRIMARY_BASIC_ACADEMICAL_CULTURE));
		assertTrue(calculateBasicAcademicalCultureGraduationService.supports(
			DUAL_BASIC_ACADEMICAL_CULTURE));
		assertFalse(calculateBasicAcademicalCultureGraduationService.supports(CORE_CULTURE));
		assertFalse(calculateBasicAcademicalCultureGraduationService.supports(COMMON_CULTURE));
	}

	@DisplayName("카테고리별 계산 시 유저의 핵심교양 상세 졸업결과를 계산한다.")
	@Test
	void shouldCalculateSingleDetailGraduationIfPrimaryCategory() {
		//given
		HashSet<BasicAcademicalCultureLecture> graduationCoreCultures = new HashSet<>(
			Set.of(BasicAcademicalCultureLecture.of(Lecture.from("KMA02128"), ICT.getName())));
		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(anyString())).willReturn(
			graduationCoreCultures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("KMA02128")
						.credit(3)
						.build())
					.build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.primaryBasicAcademicalCultureCredit(18)
			.build();

		//when
		DetailGraduationResult detailCoreCultureGraduationResult = calculateBasicAcademicalCultureGraduationService.calculateSingleDetailGraduation(
			user, PRIMARY_BASIC_ACADEMICAL_CULTURE, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailCoreCultureGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(PRIMARY_BASIC_ACADEMICAL_CULTURE, false, 18, 3.0);
	}

	@DisplayName("카테고리별 계산 시 유저의 복수전공 핵심교양 상세 졸업결과를 계산한다.")
	@Test
	void shouldCalculateSingleDetailGraduationIfDualCategory() {
		//given
		HashSet<BasicAcademicalCultureLecture> graduationBasicAcademicalCultures = new HashSet<>(
			Set.of(BasicAcademicalCultureLecture.of(Lecture.from("KMA02128"), BUSINESS.getName())));

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("KMA02128")
						.credit(3)
						.build())
					.build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.dualBasicAcademicalCultureCredit(18)
			.build();

		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(anyString())).willReturn(
			graduationBasicAcademicalCultures);

		//when
		DetailGraduationResult detailCoreCultureGraduationResult = calculateBasicAcademicalCultureGraduationService.calculateSingleDetailGraduation(
			user, DUAL_BASIC_ACADEMICAL_CULTURE, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailCoreCultureGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(DUAL_BASIC_ACADEMICAL_CULTURE, false, 18, 3.0);
	}
	@DisplayName("교환학생 인정 학점을 포함한 복수학문 기초교양 상세 졸업결과를 계산한다.")
	@Test
	void shouldCalculateSingleDetailGraduationWithExchangeCredit() {
		//given

		HashSet<BasicAcademicalCultureLecture> graduationBasicAcademicalCultures = new HashSet<>(
				Set.of(BasicAcademicalCultureLecture.of(Lecture.from("KMA02128"), BUSINESS.getName())));

		HashSet<TakenLecture> takenLectures = new HashSet<>(
				Set.of(
						TakenLecture.builder()
								.lecture(Lecture.builder()
										.id("KMA02128")
										.credit(3)
										.build())
								.build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
				.dualBasicAcademicalCultureCredit(18)
				.build();

		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(anyString())).willReturn(
				graduationBasicAcademicalCultures);

		//when
		DetailGraduationResult dualBasicAcademicalCultureDetailGraduationResult = calculateBasicAcademicalCultureGraduationService.calculateSingleDetailGraduation(
				user2, DUAL_BASIC_ACADEMICAL_CULTURE, takenLectureInventory, graduationRequirement);

		int additionalCredits = user2.getExchangeCredit().getDualBasicAcademicalCulture();
		dualBasicAcademicalCultureDetailGraduationResult.addCredit(additionalCredits);

		//then
		assertThat(dualBasicAcademicalCultureDetailGraduationResult)
				.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
				.contains(DUAL_BASIC_ACADEMICAL_CULTURE, false, 18, 9.0); // 교환학점 6 + 수강학점 3 = 9

	}
}
