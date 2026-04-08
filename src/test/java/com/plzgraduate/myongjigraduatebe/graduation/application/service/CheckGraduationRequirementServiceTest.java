package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
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
class CheckGraduationRequirementServiceTest {

	@Mock
	private FindLecturePort findLecturePort;
	@Mock
	private CalculateGraduationService calculateGraduationService;

	private CheckGraduationRequirementService checkGraduationRequirementService;

	@BeforeEach
	void setUp() {
		checkGraduationRequirementService = new CheckGraduationRequirementService(
			findLecturePort,
			calculateGraduationService
		);
	}

	@DisplayName("checkGraduationRequirement - 원본 인벤토리 복사 후 overwriteTakenCreditWithActualEarnedCredits가 호출된다.")
	@Test
	void checkGraduationRequirement_callsOverwriteTakenCredit() {
		// given
		Lecture lecture = Lecture.builder().id("KMA02101").credit(1).build();
		User anonymous = User.builder()
			.id(1L)
			.authId("anonymous")
			.primaryMajor("응용소프트웨어전공")
			.entryYear(20)
			.studentCategory(StudentCategory.NORMAL)
			.transferCredit(TransferCredit.from("0/0/0/0"))
			.exchangeCredit(ExchangeCredit.from("0/0/0/0/0/0/0/0"))
			.build();

		TakenLecture takenLecture = TakenLecture.builder().lecture(lecture).build();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(Set.of(takenLecture));

		given(findLecturePort.findLectureById("KMA02101")).willReturn(lecture);

		com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement req =
			com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement.builder()
				.primaryMajorCredit(60)
				.build();
		given(calculateGraduationService.determineGraduationRequirement(anonymous)).willReturn(req);
		given(calculateGraduationService.generateDetailGraduationResults(any(), any(), any()))
			.willReturn(List.of());
		given(calculateGraduationService.generateChapelResult(any(), any()))
			.willReturn(com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult.create(
				TakenLectureInventory.from(Set.of())));

		GraduationResult stubResult = GraduationResult.create(
			com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult.create(
				TakenLectureInventory.from(Set.of())),
			List.of()
		);
		given(calculateGraduationService.generateGraduationResult(any(), any(), any(), any(), any()))
			.willReturn(stubResult);

		// when
		GraduationResult result = checkGraduationRequirementService.checkGraduationRequirement(
			anonymous, takenLectureInventory
		);

		// then
		assertThat(result).isNotNull();
		// overwriteTakenCreditWithActualEarnedCredits 호출 검증
		org.mockito.Mockito.verify(calculateGraduationService)
			.overwriteTakenCreditWithActualEarnedCredits(any(), any(), any());
	}
}
