package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CHRISTIAN_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture.CommonGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateCommonCultureGraduationServiceTest {

	@Mock
	private FindCommonCulturePort findCommonCulturePort;

	@InjectMocks
	private CalculateCommonCultureGraduationService calculateCommonCultureGraduationService;

	@BeforeEach
	void setUp() {
		CommonGraduationManager commonGraduationManager = new CommonGraduationManager();
		calculateCommonCultureGraduationService = new CalculateCommonCultureGraduationService(
			findCommonCulturePort,
			commonGraduationManager);
	}

	@DisplayName("COMMON_CULTURE 카테고리 일때만 CommonCultureGraduationService를 호출한다.")
	@Test
	void shouldSupportCommonCultureCategory() {
		assertTrue(
			calculateCommonCultureGraduationService.supports(GraduationCategory.COMMON_CULTURE));
		assertFalse(
			calculateCommonCultureGraduationService.supports(GraduationCategory.CORE_CULTURE));
	}

	@DisplayName("유저의 공통교양 상세 졸업결과를 계산한다.")
	@Test
	void shouldCalculateCommonCulture() {
		//given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19)
			.build();
		HashSet<CommonCulture> graduationCommonCultures = new HashSet<>(
			Set.of(CommonCulture.of(Lecture.from("KMA00101"), CHRISTIAN_A)));
		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder()
					.lecture(Lecture.builder()
						.id("KMA00101")
						.credit(2)
						.build())
					.build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.commonCultureCredit(17)
			.build();

		given(findCommonCulturePort.findCommonCulture(user)).willReturn(graduationCommonCultures);

		//when
		DetailGraduationResult detailCommonCultureGraduationResult = calculateCommonCultureGraduationService.calculateSingleDetailGraduation(
			user, COMMON_CULTURE, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailCommonCultureGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(COMMON_CULTURE, false, 17, 2.0);
	}
}
