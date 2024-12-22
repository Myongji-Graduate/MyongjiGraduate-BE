package com.plzgraduate.myongjigraduatebe.completedcredit.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.CHAPEL;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.FREE_ELECTIVE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.NORMAL_CULTURE;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.FreeElectiveGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.NormalCultureGraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CompletedCredit {

	private final Long id;
	private final GraduationCategory graduationCategory;
	private double totalCredit;
	private double takenCredit;

	@Builder
	private CompletedCredit(Long id, GraduationCategory graduationCategory, double totalCredit,
		double takenCredit) {
		this.id = id;
		this.graduationCategory = graduationCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}

	public CompletedCredit from(DetailGraduationResult detailGraduationResults) {
		return CompletedCredit.builder()
			.graduationCategory(detailGraduationResults.getGraduationCategory())
			.totalCredit(detailGraduationResults.getTotalCredit())
			.takenCredit(detailGraduationResults.getTakenCredit())
			.build();
	}

	public static CompletedCredit createChapelCompletedCreditModel(ChapelResult chapelResult, User user) {
		double totalCredit = user.getStudentCategory() == StudentCategory.TRANSFER
				? 0.5 // 편입생일 경우 채플 이수 요건은 1회
				: ChapelResult.GRADUATION_COUNT / 2; // 일반 학생일 경우 기본 채플 이수 요건

		return CompletedCredit.builder()
				.graduationCategory(CHAPEL)
				.totalCredit(totalCredit)
				.takenCredit(chapelResult.getTakenChapelCredit())
				.build();
	}


	public static CompletedCredit createNormalCultureCompletedCreditModel(
		NormalCultureGraduationResult normalCultureGraduationResult
	) {
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

	public void updateCredit(double totalCredit, double takenCredit) {
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}
}
