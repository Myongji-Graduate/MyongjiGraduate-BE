package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.CORE_CULTURE;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.CULTURE_ART;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture.CoreGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CalculateCoreCultureGraduationServiceTest {

	@Mock
	private FindCoreCulturePort findCoreCulturePort;
	private CalculateCoreCultureGraduationService calculateCoreCultureGraduationService;

	@BeforeEach
	void setUp() {
		calculateCoreCultureGraduationService = new CalculateCoreCultureGraduationService(findCoreCulturePort, new CoreGraduationManager());
	}

	@DisplayName("CORE_CULTURE 카테고리 일때만 CoreCultureGraduationService를 호출한다.")
	@Test
	public void supportsTest() {
		assertTrue(calculateCoreCultureGraduationService.supports(GraduationCategory.CORE_CULTURE));
		assertFalse(calculateCoreCultureGraduationService.supports(GraduationCategory.COMMON_CULTURE));
	}

	@DisplayName("유저의 핵심교양 상세 졸업결과를 계산한다.")
	@Test
	void calculateCoreCulture() {
		//given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19)
			.build();
		HashSet<CoreCulture> graduationCoreCultures = new HashSet<>(
			Set.of(CoreCulture.of(Lecture.from("KMA02128"), CULTURE_ART)));
		int coreCultureTotalCredit = 12;

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder().lecture(Lecture.builder()
					.lectureCode("KMA02128")
					.credit(3).build()).build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.coreCultureCredit(coreCultureTotalCredit).build();

		given(findCoreCulturePort.findCoreCulture(user)).willReturn(graduationCoreCultures);

		//when
		DetailGraduationResult detailCoreCultureGraduationResult = calculateCoreCultureGraduationService.calculateSingleDetailGraduation(
			user, CORE_CULTURE, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailCoreCultureGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(CORE_CULTURE, false, 12, 3.0);
	}

}
