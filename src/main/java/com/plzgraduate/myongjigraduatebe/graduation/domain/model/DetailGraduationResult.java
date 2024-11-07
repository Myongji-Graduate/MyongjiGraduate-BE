package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationResult {

	private final boolean isCompleted;
	private final int totalCredit;
	private final List<DetailCategoryResult> detailCategory;
	private GraduationCategory graduationCategory;
	private double takenCredit;

	@Builder
	private DetailGraduationResult(GraduationCategory graduationCategory, boolean isCompleted,
		int totalCredit,
		double takenCredit,
		List<DetailCategoryResult> detailCategory) {
		this.graduationCategory = graduationCategory;
		this.isCompleted = isCompleted;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.detailCategory = detailCategory;
	}

	public static DetailGraduationResult create(GraduationCategory graduationCategory,
		int totalCredit,
		List<DetailCategoryResult> detailCategoryResults) {
		return DetailGraduationResult.builder()
			.graduationCategory(graduationCategory)
			.isCompleted(checkIsCompleted(detailCategoryResults))
			.totalCredit(totalCredit)
			.takenCredit(calculateTakenCredit(detailCategoryResults))
			.detailCategory(detailCategoryResults)
			.build();
	}

	public static DetailGraduationResult createNonCategorizedGraduationResult(int totalCredit,
		List<DetailCategoryResult> detailCategoryResults) {
		return DetailGraduationResult.builder()
			.isCompleted(checkIsCompleted(detailCategoryResults))
			.totalCredit(totalCredit)
			.takenCredit(calculateTakenCredit(detailCategoryResults))
			.detailCategory(detailCategoryResults)
			.build();
	}

	private static boolean checkIsCompleted(List<DetailCategoryResult> detailCategoryResults) {
		return detailCategoryResults.stream()
			.allMatch(DetailCategoryResult::isCompleted);
	}

	private static int calculateTakenCredit(List<DetailCategoryResult> detailCategoryResults) {
		return detailCategoryResults.stream()
			.mapToInt(DetailCategoryResult::getTakenCredits)
			.sum();
	}

	public void assignGraduationCategory(GraduationCategory graduationCategory) {
		this.graduationCategory = graduationCategory;
	}

	public void addCredit(double takenCredit) {
		this.takenCredit += takenCredit;
	}

	public int getNormalLeftCredit() {
		return detailCategory.stream()
			.mapToInt(DetailCategoryResult::getNormalLeftCredit)
			.sum();
	}

	public int getFreeElectiveLeftCredit() {
		return detailCategory.stream()
			.mapToInt(DetailCategoryResult::getFreeElectiveLeftCredit)
			.sum();
	}
}
