package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FreeElectiveGraduationResult {

	private final String categoryName;
	private boolean isCompleted;
	private final int totalCredit;
	private final int takenCredit;

	@Builder
	private FreeElectiveGraduationResult(String categoryName, boolean isCompleted, int totalCredit, int takenCredit) {
		this.categoryName = categoryName;
		this.isCompleted = isCompleted;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}

	public static FreeElectiveGraduationResult create(int totalCredit, TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults, int leftNormalCultureCredit) {
		return FreeElectiveGraduationResult.builder()
			.categoryName(FREE_ELECTIVE.getName())
			.isCompleted(false)
			.totalCredit(totalCredit)
			.takenCredit(
				calculateTakenCredit(takenLectureInventory, detailGraduationResults, leftNormalCultureCredit))
			.build();
	}

	public void checkCompleted() {
		this.isCompleted = takenCredit >= totalCredit;
	}

	private static int calculateTakenCredit(TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults, int leftNormalCultureCredit) {
		int remainCreditByDetailGraduationResult = detailGraduationResults.stream()
			.mapToInt(DetailGraduationResult::getFreeElectiveLeftCredit)
			.sum();

		int remainCreditByTakenLectures = takenLectureInventory.getTakenLectures().stream()
			.mapToInt(takenLecture -> takenLecture.getLecture().getCredit())
			.sum();

		return remainCreditByDetailGraduationResult + remainCreditByTakenLectures + leftNormalCultureCredit;
	}
}
