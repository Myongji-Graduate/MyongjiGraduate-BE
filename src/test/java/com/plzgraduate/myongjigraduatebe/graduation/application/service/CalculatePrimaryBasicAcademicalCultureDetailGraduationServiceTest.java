package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.ICT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CalculatePrimaryBasicAcademicalCultureDetailGraduationServiceTest {

	@Mock
	private FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;
	@InjectMocks
	private CalculatePrimaryBasicAcademicalCultureDetailGraduationService calculatePrimaryBasicAcademicalCultureDetailGraduationService;

	@DisplayName("유저의 핵심교양 상세 졸업결과를 계산한다.")
	@Test
	void calculateCoreCulture() {
		//given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19).build();
		HashSet<BasicAcademicalCultureLecture> graduationCoreCultures = new HashSet<>(
			Set.of(BasicAcademicalCultureLecture.of(Lecture.from("KMA02128"), ICT.getName())));
		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(user.getPrimaryMajor())).willReturn(graduationCoreCultures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder().lecture(Lecture.builder()
					.lectureCode("KMA02128")
					.credit(3).build()).build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.primaryBasicAcademicalCultureCredit(18).build();

		//when
		DetailGraduationResult detailCoreCultureGraduationResult = calculatePrimaryBasicAcademicalCultureDetailGraduationService.calculateDetailGraduation(
			user, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailCoreCultureGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(PRIMARY_BASIC_ACADEMICAL_CULTURE, false, 18, 3.0);
	}

}
