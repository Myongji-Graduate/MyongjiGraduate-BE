package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CalculatePrimaryMandatoryMajorDetailGraduationServiceTest {

	@Mock
	private FindMajorPort findMajorPort;
	@InjectMocks
	private CalculatePrimaryMandatoryMajorDetailGraduationService calculatePrimaryMandatoryMajorDetailGraduationService;

	@DisplayName("유저의 주전공필수 졸업결과를 계산한다.")
	@Test
	void calculateCoreCulture() {
		//given
		User user = User.builder()
			.id(1L)
			.primaryMajor("응용소프트웨어전공")
			.entryYear(19).build();
		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder().lectureCode("HEC01211").credit(3).build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder().lectureCode("HEC01204").credit(3).build(), "응용소프트웨어전공", 1, 16, 23)));
		given(findMajorPort.findMajor(user.getPrimaryMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder().lecture(Lecture.builder()
					.lectureCode("HEC01211") //전공 필수
					.credit(3).build()).build(),
				TakenLecture.builder().lecture(Lecture.builder()
					.lectureCode("HEC01305") //전공 선택
					.credit(3).build()).build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.primaryMajorCredit(70).build();

		//when
		DetailGraduationResult detailPrimaryMandatoryMajorGraduationResult = calculatePrimaryMandatoryMajorDetailGraduationService.calculateDetailGraduation(
			user, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailPrimaryMandatoryMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(PRIMARY_MANDATORY_MAJOR, false, 6, 3.0);
	}

	@DisplayName("주전공 졸업결과에서 주전공필수 졸업결과를 분리한다.")
	@Test
	void isolatePrimaryElectiveMajorDetailGraduation() {
		//given
		DetailCategoryResult primaryMandatoryMajorDetailCategoryResult = DetailCategoryResult.builder()
			.detailCategoryName("전공필수")
			.totalCredits(18)
			.takenCredits(18)
			.build();
		DetailCategoryResult primaryElectiveMajorDetailCategoryResult = DetailCategoryResult.builder()
			.detailCategoryName("전공선택")
			.totalCredits(52)
			.takenCredits(52)
			.build();
		DetailGraduationResult primaryMajorDetailGraduationResult = DetailGraduationResult.createMajorDetailGraduationResult(
			70, List.of(primaryMandatoryMajorDetailCategoryResult, primaryElectiveMajorDetailCategoryResult));

		//when
		DetailGraduationResult primaryMandatoryMajorDetailGraduationResult = calculatePrimaryMandatoryMajorDetailGraduationService.isolatePrimaryMandatoryMajorDetailGraduation(
			primaryMajorDetailGraduationResult);

		//then
		assertThat(primaryMandatoryMajorDetailGraduationResult)
			.extracting("graduationCategory", "totalCredit", "takenCredit")
			.contains(
				PRIMARY_MANDATORY_MAJOR,
				primaryMandatoryMajorDetailCategoryResult.getTotalCredits(),
				primaryMandatoryMajorDetailCategoryResult.getTakenCredits());
	}

}
