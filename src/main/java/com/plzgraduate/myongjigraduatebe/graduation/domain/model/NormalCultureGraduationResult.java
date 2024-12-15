package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.NORMAL_CULTURE;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NormalCultureGraduationResult {

	private static final String VOLUNTEER_CREDIT_CODE = "KMA02198";

	private final String categoryName;
	private final int totalCredit;
	private boolean isCompleted;
	private int takenCredit;

	@Builder
	private NormalCultureGraduationResult(
		String categoryName,
		boolean isCompleted,
		int totalCredit,
		int takenCredit
	) {
		this.categoryName = categoryName;
		this.isCompleted = isCompleted;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}

	public static NormalCultureGraduationResult create(
		int totalCredit,
		TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults
	) {
		return NormalCultureGraduationResult.builder()
			.categoryName(NORMAL_CULTURE.getName())
			.isCompleted(false)
			.totalCredit(totalCredit)
			.takenCredit(calculateTakenCredit(takenLectureInventory, detailGraduationResults))
			.build();
	}

	private static int calculateTakenCredit(
		TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults
	) {
		int remainCreditByDetailGraduationResult = detailGraduationResults.stream()
			.mapToInt(DetailGraduationResult::getNormalLeftCredit)
			.sum();
		Set<TakenLecture> remainTakenNormalCultures = takenLectureInventory.getCultureLectures()
			.stream()
			.filter(takenLecture -> !takenLecture.getLecture().getId()
				.equals(VOLUNTEER_CREDIT_CODE))
			.collect(Collectors.toSet());
		int remainCreditByTakenLectures = remainTakenNormalCultures.stream()
			.mapToInt(takenLecture -> takenLecture.getLecture().getCredit())
			.sum();
		takenLectureInventory.handleFinishedTakenLectures(remainTakenNormalCultures);
		return remainCreditByDetailGraduationResult + remainCreditByTakenLectures;
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
}
