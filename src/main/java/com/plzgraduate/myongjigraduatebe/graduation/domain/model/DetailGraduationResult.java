package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationResult {

	private final String categoryName;
	private final boolean isCompleted;
	private final int totalCredit;
	private final int takenCredit;
	private final List<DetailCategoryResult> detailCategory;

	@Builder
	private DetailGraduationResult(String categoryName, boolean isCompleted, int totalCredit, int takenCredit,
		List<DetailCategoryResult> detailCategory) {
		this.categoryName = categoryName;
		this.isCompleted = isCompleted;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.detailCategory = detailCategory;
	}

	public static DetailGraduationResult create(GraduationCategory graduationCategory, int totalCredit, List<DetailCategoryResult> detailCategoryResults) {
		return DetailGraduationResult.builder()
			.categoryName(graduationCategory.name())
			.isCompleted(checkIsCompleted(detailCategoryResults))
			.totalCredit(totalCredit)
			.takenCredit(calculateTakenCredit(detailCategoryResults))
			.detailCategory(detailCategoryResults)
			.build();
	}

	public int getLeftCredit() {
		if(totalCredit>=takenCredit) {
			return 0;
		}
		return takenCredit - totalCredit;
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
}
