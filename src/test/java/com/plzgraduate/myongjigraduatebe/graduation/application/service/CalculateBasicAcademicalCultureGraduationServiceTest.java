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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy.CandidateLecture;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.FindOptionalMandatoryPolicyPort;
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
	@Mock
	private FindOptionalMandatoryPolicyPort findOptionalMandatoryPolicyPort;

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
			findBasicAcademicalCulturePort, findOptionalMandatoryPolicyPort,
			basicAcademicalGraduationManagers);
		lenient().when(findOptionalMandatoryPolicyPort.findActiveBasicPolicies(
			anyString(), anyInt())).thenReturn(List.of());

		user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.dualMajor("경영학전공")
			.studentCategory(DUAL_MAJOR)
			.entryYear(19)
			.build();

		 user2 = User.builder()
				.id(1L)
				.primaryMajor("응용소프트웨어전공")
				.dualMajor("경영학전공")
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
		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(anyString(), anyInt())).willReturn(
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

		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(anyString(), anyInt())).willReturn(
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

		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(anyString(), anyInt())).willReturn(
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
	@Test
	@DisplayName("복수전공 사용자의 모든 졸업 결과를 계산해야 한다.")
	void shouldCalculateAllResultsForDualMajor() {
		Set<BasicAcademicalCultureLecture> primaryLectures = Set.of(BasicAcademicalCultureLecture.of(Lecture.from("KMA02128"), "ICT"));
		Set<BasicAcademicalCultureLecture> dualLectures = Set.of(BasicAcademicalCultureLecture.of(Lecture.from("KMA02129"), "BUSINESS"));

		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture("응용소프트웨어전공",19)).willReturn(primaryLectures);
		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture("경영학전공",19)).willReturn(dualLectures);

		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
				TakenLecture.builder().lecture(Lecture.builder().id("KMA02128").credit(3).build()).build(),
				TakenLecture.builder().lecture(Lecture.builder().id("KMA02129").credit(3).build()).build()
		));

		GraduationRequirement requirement = GraduationRequirement.builder()
				.primaryBasicAcademicalCultureCredit(18)
				.dualBasicAcademicalCultureCredit(18)
				.build();

		List<DetailGraduationResult> results = calculateBasicAcademicalCultureGraduationService.calculateAllDetailGraduation(user2, inventory, requirement);

		assertThat(results).hasSize(2);

		DetailGraduationResult primaryResult = results.get(0);
		assertThat(primaryResult)
				.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
				.containsExactly(GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE, false, 18, 3.0);

		DetailGraduationResult dualResult = results.get(1);
		assertThat(dualResult)
				.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
				.containsExactly(GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE, false, 18, 9.0);
	}

	@Test
	@DisplayName("과거 학번의 현재 전공명을 College가 찾지 못해도 기본 학문기초 계산을 수행한다.")
	void calculateLegacyRenamedMajorWithDefaultManager() {
		User legacyGlobalBusinessUser = User.builder()
			.id(2L)
			.primaryMajor("글로벌비즈니스학전공")
			.entryYear(23)
			.build();
		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(
			"글로벌비즈니스학전공", 23)).willReturn(Set.of());
		GraduationRequirement requirement = GraduationRequirement.builder()
			.primaryBasicAcademicalCultureCredit(6)
			.build();

		DetailGraduationResult result =
			calculateBasicAcademicalCultureGraduationService.calculateSingleDetailGraduation(
				legacyGlobalBusinessUser,
				PRIMARY_BASIC_ACADEMICAL_CULTURE,
				TakenLectureInventory.from(Set.of()),
				requirement
			);

		assertThat(result)
			.extracting("graduationCategory", "totalCredit", "takenCredit")
			.containsExactly(PRIMARY_BASIC_ACADEMICAL_CULTURE, 6, 0.0);
	}

	@Test
	@DisplayName("총 학점을 채워도 전공별 지정필수를 충족하지 못하면 미완료로 판정한다.")
	void requiresMajorSpecificMandatoryPolicyInAdditionToTotalCredit() {
		Lecture optionalLecture = Lecture.builder()
			.id("OPTIONAL")
			.name("일반 학문기초")
			.credit(3)
			.duplicateCode("OPTIONAL")
			.build();
		Lecture mandatoryLecture = Lecture.builder()
			.id("MANDATORY")
			.name("미적분학1")
			.credit(3)
			.duplicateCode("MANDATORY")
			.build();
		User statisticsUser = User.builder()
			.id(3L)
			.primaryMajor("응용통계학전공")
			.entryYear(25)
			.build();
		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(
			"응용통계학전공", 25)).willReturn(Set.of(
			BasicAcademicalCultureLecture.of(optionalLecture, "사회과학대")
		));
		given(findOptionalMandatoryPolicyPort.findActiveBasicPolicies(
			"응용통계학전공", 25)).willReturn(List.of(
			OptionalMandatoryPolicy.builder()
				.name("응용통계 지정필수")
				.major("응용통계학전공")
				.requiredCount(1)
				.requiredCredit(3)
				.candidateLectures(List.of(
					new CandidateLecture(mandatoryLecture, "MANDATORY")
				))
				.build()
		));

		DetailGraduationResult result =
			calculateBasicAcademicalCultureGraduationService.calculateSingleDetailGraduation(
				statisticsUser,
				PRIMARY_BASIC_ACADEMICAL_CULTURE,
				TakenLectureInventory.from(Set.of(
					TakenLecture.builder().lecture(optionalLecture).build()
				)),
				GraduationRequirement.builder()
					.primaryBasicAcademicalCultureCredit(3)
					.build()
			);

		assertThat(result.isCompleted()).isFalse();
		assertThat(result.getDetailCategory().get(0).getHaveToLectures())
			.contains(mandatoryLecture);
	}

}
