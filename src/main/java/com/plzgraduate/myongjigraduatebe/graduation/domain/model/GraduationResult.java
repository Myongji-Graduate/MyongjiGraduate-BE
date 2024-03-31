package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

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
	private double takenCredit;
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
		addUpTotalCredit();
		addUpTakenCredit();
		addUpChapelTakenCreditToCommonCulture();

		boolean isAllDetailGraduationResultCompleted = detailGraduationResults.stream()
			.allMatch(DetailGraduationResult::isCompleted);
		this.graduated = chapelResult.isCompleted() && isAllDetailGraduationResultCompleted
			&& normalCultureGraduationResult.isCompleted() && freeElectiveGraduationResult.isCompleted();
	}

	private void addUpTotalCredit() {
		this.totalCredit = detailGraduationResults.stream()
			.mapToInt(DetailGraduationResult::getTotalCredit)
			.sum()
			+ normalCultureGraduationResult.getTotalCredit()
			+ freeElectiveGraduationResult.getTotalCredit();
	}

	private void addUpTakenCredit() {
		this.takenCredit = detailGraduationResults.stream()
			.mapToDouble(DetailGraduationResult::getTakenCredit)
			.sum()
			+ normalCultureGraduationResult.getTakenCredit()
			+ freeElectiveGraduationResult.getTakenCredit()
			+ chapelResult.getTakenChapelCredit();
	}

	private void addUpChapelTakenCreditToCommonCulture() {
		this.detailGraduationResults.stream()
			.filter(detailGraduationResult -> detailGraduationResult.getGraduationCategory() ==COMMON_CULTURE)
			.forEach(
				detailGraduationResult -> detailGraduationResult.addCredit(this.chapelResult.getTakenChapelCredit()));
	}

	private void handleLeftTakenNormaCulture(TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		this.normalCultureGraduationResult = NormalCultureGraduationResult.create(
			graduationRequirement.getNormalCultureCredit(), takenLectureInventory, detailGraduationResults);

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
