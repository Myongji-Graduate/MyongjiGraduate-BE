package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GraduationResult {

	private final ChapelResult chapelResult;
	private final List<DetailGraduationResult> detailGraduationResults;
	private NormalCultureGraduationResult normalCultureGraduationResult;
	private FreeElectiveGraduationResult freeElectiveGraduationResult;
	private int totalCredit;
	private int takenCredit;
	private boolean graduated;

	@Builder
	private GraduationResult(ChapelResult chapelResult, List<DetailGraduationResult> detailGraduationResults,
		NormalCultureGraduationResult normalCultureGraduationResult,
		FreeElectiveGraduationResult freeElectiveGraduationResult, int totalCredit, int takenCredit,
		boolean graduated) {
		this.chapelResult = chapelResult;
		this.detailGraduationResults = detailGraduationResults;
		this.normalCultureGraduationResult = normalCultureGraduationResult;
		this.freeElectiveGraduationResult = freeElectiveGraduationResult;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduated = graduated;
	}

	public static GraduationResult create(ChapelResult chapelResult,
		List<DetailGraduationResult> detailGraduationResults) {
		return GraduationResult.builder()
			.chapelResult(chapelResult)
			.detailGraduationResults(detailGraduationResults)
			.totalCredit(0)
			.takenCredit(0)
			.graduated(false)
			.build();
	}

	public void handleLeftTakenLectures(TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		handleLeftTakenNormaCulture(takenLectureInventory, graduationRequirement);
		handleLeftTakenFreeElective(takenLectureInventory, graduationRequirement);
	}

	public void checkGraduated() {
		this.totalCredit = detailGraduationResults.stream()
			.mapToInt(DetailGraduationResult::getTotalCredit)
			.sum()
			+ 2 + normalCultureGraduationResult.getTotalCredit() + freeElectiveGraduationResult.getTotalCredit();

		this.takenCredit = detailGraduationResults.stream()
			.mapToInt(DetailGraduationResult::getTakenCredit)
			.sum()
			+ normalCultureGraduationResult.getTakenCredit() + freeElectiveGraduationResult.getTakenCredit()
			+ (chapelResult.isCompleted() ? 2 : 0);

		boolean isAllDetailGraduationResultCompleted = detailGraduationResults.stream()
			.allMatch(DetailGraduationResult::isCompleted);

		this.graduated = chapelResult.isCompleted() && isAllDetailGraduationResultCompleted
			&& normalCultureGraduationResult.isCompleted() && freeElectiveGraduationResult.isCompleted();
	}

	private void handleLeftTakenNormaCulture(TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		this.normalCultureGraduationResult = NormalCultureGraduationResult.create(
			graduationRequirement.getNormalCultureCredit(), takenLectureInventory,
			detailGraduationResults);

		normalCultureGraduationResult.checkCompleted();
	}

	private void handleLeftTakenFreeElective(TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		int leftNormalCultureCredit = normalCultureGraduationResult.getLeftCredit();
		this.freeElectiveGraduationResult = FreeElectiveGraduationResult.create(
			graduationRequirement.getFreeElectiveCredit(), takenLectureInventory, detailGraduationResults,
			leftNormalCultureCredit);

		freeElectiveGraduationResult.checkCompleted();
	}
}
