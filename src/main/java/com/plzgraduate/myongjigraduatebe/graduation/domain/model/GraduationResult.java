package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.List;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
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
	private GraduationResult(
		ChapelResult chapelResult,
		List<DetailGraduationResult> detailGraduationResults,
		NormalCultureGraduationResult normalCultureGraduationResult,
		FreeElectiveGraduationResult freeElectiveGraduationResult,
		int totalCredit,
		int takenCredit,
		boolean graduated
	) {
		this.chapelResult = chapelResult;
		this.detailGraduationResults = detailGraduationResults;
		this.normalCultureGraduationResult = normalCultureGraduationResult;
		this.freeElectiveGraduationResult = freeElectiveGraduationResult;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduated = graduated;
	}

	public static GraduationResult create(
		ChapelResult chapelResult, List<DetailGraduationResult> detailGraduationResults
	) {
		return GraduationResult.builder().chapelResult(chapelResult)
			.detailGraduationResults(detailGraduationResults).totalCredit(0).takenCredit(0)
			.graduated(false).build();
	}

	public void handleLeftTakenLectures(
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement, User user
	) {
		handleLeftTakenNormaCulture(takenLectureInventory, graduationRequirement);
		handleLeftTakenFreeElective(takenLectureInventory, graduationRequirement, user);
	}

	public void checkGraduated(GraduationRequirement graduationRequirement, User user) {
		addUpTotalCredit(graduationRequirement.getTotalCredit());
		addUpTakenCredit(user);

		boolean isAllDetailGraduationResultCompleted = detailGraduationResults.stream()
			.allMatch(DetailGraduationResult::isCompleted);
		this.graduated = chapelResult.isCompleted() && isAllDetailGraduationResultCompleted
			&& normalCultureGraduationResult.isCompleted()
			&& freeElectiveGraduationResult.isCompleted();
	}

	public void deductDuplicatedCredit(int duplicatedCredit) {
		this.takenCredit -= duplicatedCredit;
	}

	private void addUpTotalCredit(int originTotalCredit) {
		int combinedScore =
			detailGraduationResults.stream().mapToInt(DetailGraduationResult::getTotalCredit).sum()
				+ normalCultureGraduationResult.getTotalCredit()
				+ freeElectiveGraduationResult.getTotalCredit();
		if (originTotalCredit < combinedScore) {
			this.totalCredit = originTotalCredit;
			return;
		}
		this.totalCredit = combinedScore;
	}

	private void addUpTakenCredit(User user) {
		this.takenCredit = detailGraduationResults.stream()
				.filter(result ->
//						result.getGraduationCategory() != GraduationCategory.TRANSFER_COMBINED_CULTURE &&
								result.getGraduationCategory() != GraduationCategory.TRANSFER_CHRISTIAN &&
								result.getGraduationCategory() != GraduationCategory.FREE_ELECTIVE
				)
				.mapToDouble(result -> {
					System.out.println("Category: " + result.getGraduationCategory());
					System.out.println("Taken Credit: " + result.getTakenCredit());
					return result.getTakenCredit();
				})
				.sum();

		double freeElectiveCredits = freeElectiveGraduationResult.getTakenCredit();
		double normalCultureCredits = normalCultureGraduationResult.getTakenCredit();
		//double transferCredits = calculateTransferCredits();

		System.out.println("Free Elective Graduation Result Taken Credit: " + freeElectiveCredits);
		System.out.println("Normal Culture Graduation Result Taken Credit: " + normalCultureCredits);
		//System.out.println("Transfer Combined Culture Credits: " + transferCredits);

		if (user.getStudentCategory() == StudentCategory.TRANSFER) {
			double transferChristianCredit = user.getTransferCredit().getChristianLecture();
			double transferFreeElectiveCredit = user.getTransferCredit().getFreeElective();

			System.out.println("Transfer Christian Credit: " + transferChristianCredit);
			System.out.println("Transfer Free Elective Credit: " + transferFreeElectiveCredit);

			this.takenCredit += transferChristianCredit;
		}

		this.takenCredit += freeElectiveCredits + normalCultureCredits;

		System.out.println("Total Taken Credit (including additional categories): " + this.takenCredit);
	}



	private double calculateTransferCredits() {
		return detailGraduationResults.stream()
				.filter(result ->
						result.getGraduationCategory() == GraduationCategory.TRANSFER_COMBINED_CULTURE
				)
				.mapToDouble(DetailGraduationResult::getTakenCredit)
				.sum();
	}


	private void handleLeftTakenNormaCulture(
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement
	) {
		this.normalCultureGraduationResult = NormalCultureGraduationResult.create(
			graduationRequirement.getNormalCultureCredit(),
			takenLectureInventory,
			detailGraduationResults
		);

		normalCultureGraduationResult.checkCompleted();
	}

	private void handleLeftTakenFreeElective(
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement, User user
	) {
		int leftNormalCultureCredit = normalCultureGraduationResult.getLeftCredit();
		this.freeElectiveGraduationResult = FreeElectiveGraduationResult.create(
			graduationRequirement.getFreeElectiveCredit(),
			takenLectureInventory,
			detailGraduationResults,
			leftNormalCultureCredit,
			user
		);

		freeElectiveGraduationResult.checkCompleted();
	}
}
