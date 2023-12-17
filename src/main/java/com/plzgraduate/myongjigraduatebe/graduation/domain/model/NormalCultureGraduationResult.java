package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

import java.util.List;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NormalCultureGraduationResult {

	private final String categoryName;
	private boolean isCompleted;
	private final int totalCredit;
	private int takenCredit;

	@Builder
	private NormalCultureGraduationResult(String categoryName, boolean isCompleted, int totalCredit, int takenCredit) {
		this.categoryName = categoryName;
		this.isCompleted = isCompleted;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}

	public static NormalCultureGraduationResult create(int totalCredit, TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults) {
		return NormalCultureGraduationResult.builder()
			.categoryName(NORMAL_CULTURE.getName())
			.isCompleted(false)
			.totalCredit(totalCredit)
			.takenCredit(calculateTakenCredit(takenLectureInventory, detailGraduationResults)).build();
	}

	public void checkCompleted() {
		this.isCompleted = takenCredit >= totalCredit;
	}

	public int getLeftCredit() {
		if (totalCredit >= takenCredit) {
			return 0;
		}
		int leftCredit = takenCredit - totalCredit;
		this.takenCredit = totalCredit;
		return leftCredit;
	}

	private static int calculateTakenCredit(TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults) {
		int remainCreditByDetailGraduationResult = detailGraduationResults.stream()
			.mapToInt(DetailGraduationResult::getNormalLeftCredit)
			.sum();

		Set<TakenLecture> remainTakenNormalCultures = takenLectureInventory.getCultureLectures();
		int remainCreditByTakenLectures = remainTakenNormalCultures.stream()
			.mapToInt(takenLecture -> takenLecture.getLecture().getCredit())
			.sum();

		takenLectureInventory.handleFinishedTakenLectures(remainTakenNormalCultures);
		return remainCreditByDetailGraduationResult + remainCreditByTakenLectures;
	}
}
