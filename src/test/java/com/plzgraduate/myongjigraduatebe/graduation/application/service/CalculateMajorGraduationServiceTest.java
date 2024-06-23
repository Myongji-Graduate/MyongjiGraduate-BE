package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_MANDATORY_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CalculateMajorGraduationServiceTest {

	@Mock
	private FindMajorPort findMajorPort;

	@InjectMocks
	private CalculateMajorGraduationService calculateMajorGraduationService;

	@DisplayName("유저의 주전공필수 졸업결과를 계산한다.")
	@Test
	void calculateSingleDetailGraduationIfPrimaryMandatory() {
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
			.entryYear(19).build();
		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				// 전공 필수
				MajorLecture.of(Lecture.builder().lectureCode("HEC01211").credit(3).build(), "응용소프트웨어전공", 1, 16, 23),
				// 전공 선택
				MajorLecture.of(Lecture.builder().lectureCode("HEC01305").credit(3).build(), "응용소프트웨어전공", 0, 16, 23),
				// 전공 선택
				MajorLecture.of(Lecture.builder().lectureCode("HEC01318").credit(3).build(), "응용소프트웨어전공", 0, 16, 23)));
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
			.dualMajor("응용소프트웨어전공")
			.entryYear(19).build();
		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder().lectureCode("HEC01211").credit(3).build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder().lectureCode("HEC01204").credit(3).build(), "응용소프트웨어전공", 1, 16, 23)));
		given(findMajorPort.findMajor(user.getDualMajor())).willReturn(graduationMajorLectures);

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
			.dualMajorCredit(70).build();

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
			.dualMajor("응용소프트웨어전공")
			.entryYear(19).build();
		HashSet<MajorLecture> graduationMajorLectures = new HashSet<>(
			Set.of(
				MajorLecture.of(Lecture.builder().lectureCode("HEC01211").credit(3).build(), "응용소프트웨어전공", 1, 16, 23),
				MajorLecture.of(Lecture.builder().lectureCode("HEC01304").credit(3).build(), "응용소프트웨어전공", 0, 16, 23)));
		given(findMajorPort.findMajor(user.getDualMajor())).willReturn(graduationMajorLectures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(
				TakenLecture.builder().lecture(Lecture.builder()
					.lectureCode("HEC01211") //전공 필수
					.credit(3).build()).build(),
				TakenLecture.builder().lecture(Lecture.builder()
					.lectureCode("HEC01304") //전공 선택
					.credit(3).build()).build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.dualMajorCredit(70).build();

		//when
		DetailGraduationResult detailDualMandatoryMajorGraduationResult = calculateMajorGraduationService.calculateSingleDetailGraduation(
			user, DUAL_ELECTIVE_MAJOR, takenLectureInventory, graduationRequirement);

		//then
		assertThat(detailDualMandatoryMajorGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(DUAL_ELECTIVE_MAJOR, false, 67, 3.0);
	}
}
