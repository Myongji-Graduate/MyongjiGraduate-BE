package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CheckGraduationRequirementUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CheckGraduationRequirementService implements CheckGraduationRequirementUseCase {

	private final FindLecturePort findLecturePort;
	private final CalculateGraduationService calculateGraduationService;

	@Override
	public GraduationResult checkGraduationRequirement(
		User anonymous,
		TakenLectureInventory takenLectureInventory
	) {
		Set<TakenLecture> takenLectureWithDuplicateCode = getTakenLectureWithDuplicateCode(
			anonymous,
			takenLectureInventory
		);

		TakenLectureInventory takenLectureInventoryWithDuplicateCode =
			TakenLectureInventory.from(takenLectureWithDuplicateCode);

		GraduationRequirement graduationRequirement =
			calculateGraduationService.determineGraduationRequirement(anonymous);

		List<DetailGraduationResult> detailGraduationResults = calculateGraduationService.generateDetailGraduationResults(
			anonymous,
			takenLectureInventoryWithDuplicateCode,
			graduationRequirement
		);

		ChapelResult chapelResult = calculateGraduationService.generateChapelResult(
			anonymous,
			takenLectureInventoryWithDuplicateCode
		);

		if (anonymous.getStudentCategory() == StudentCategory.TRANSFER) {
			chapelResult.checkAnonymousTransferUserChapelCount();
		}

		GraduationResult graduationResult = calculateGraduationService.generateGraduationResult(
			chapelResult,
			detailGraduationResults,
			takenLectureInventoryWithDuplicateCode,
			graduationRequirement,
			anonymous
		);
		calculateGraduationService.handleDuplicatedTakenCredit(anonymous, graduationResult);

		return graduationResult;
	}

	private Set<TakenLecture> getTakenLectureWithDuplicateCode(
		User anonymous,
		TakenLectureInventory takenLectureInventory
	) {
		return takenLectureInventory.getTakenLectures()
			.stream()
			.map(takenLecture -> TakenLecture.of(
					anonymous,
					findLecturePort.findLectureById(takenLecture.getLecture().getId()),
					takenLecture.getYear(),
					takenLecture.getSemester()
				)
			).collect(Collectors.toSet());
	}
}
