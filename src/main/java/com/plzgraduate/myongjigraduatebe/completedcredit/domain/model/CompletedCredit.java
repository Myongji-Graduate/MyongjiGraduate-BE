package com.plzgraduate.myongjigraduatebe.completedcredit.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.CHAPEL;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.FREE_ELECTIVE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.NORMAL_CULTURE;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.FreeElectiveGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.NormalCultureGraduationResult;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CompletedCredit {

	private final Long id;
	private final GraduationCategory graduationCategory;
	private int totalCredit;
	private double takenCredit;

	@Builder
	private CompletedCredit(Long id, GraduationCategory graduationCategory, int totalCredit,
		double takenCredit) {
		this.id = id;
		this.graduationCategory = graduationCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}

	public static CompletedCredit from(DetailGraduationResult detailGraduationResults) {
		return CompletedCredit.builder()
			.graduationCategory(detailGraduationResults.getGraduationCategory())
			.totalCredit(detailGraduationResults.getTotalCredit())
			.takenCredit(detailGraduationResults.getTakenCredit())
			.build();
	}

	public static CompletedCredit createChapelCompletedCreditModel(ChapelResult chapelResult) {
		return CompletedCredit.builder()
			.graduationCategory(CHAPEL)
			.totalCredit(ChapelResult.GRADUATION_COUNT / 2)
			.takenCredit(chapelResult.getTakenChapelCredit())
			.build();
	}

	public static CompletedCredit createNormalCultureCompletedCreditModel(
		NormalCultureGraduationResult normalCultureGraduationResult) {
		return CompletedCredit.builder()
			.graduationCategory(NORMAL_CULTURE)
			.totalCredit(normalCultureGraduationResult.getTotalCredit())
			.takenCredit(normalCultureGraduationResult.getTakenCredit())
			.build();
	}

	public static CompletedCredit createFreeElectiveCompletedCreditModel(
		FreeElectiveGraduationResult freeElectiveGraduationResult) {
		return CompletedCredit.builder()
			.graduationCategory(FREE_ELECTIVE)
			.totalCredit(freeElectiveGraduationResult.getTotalCredit())
			.takenCredit(freeElectiveGraduationResult.getTakenCredit())
			.build();
	}

	public void updateCredit(int totalCredit, double takenCredit) {
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}
}
