package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.FREE_ELECTIVE;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.List;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FreeElectiveGraduationResult {

	private final String categoryName;
	private final int totalCredit;
	private final int takenCredit;
	private boolean isCompleted;

	@Builder
	private FreeElectiveGraduationResult(String categoryName, boolean isCompleted, int totalCredit,
		int takenCredit) {
		this.categoryName = categoryName;
		this.isCompleted = isCompleted;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}

	public static FreeElectiveGraduationResult create(int totalCredit,
		TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults, int leftNormalCultureCredit, User user) {
		return FreeElectiveGraduationResult.builder()
			.categoryName(FREE_ELECTIVE.getName())
			.isCompleted(false)
			.totalCredit(totalCredit)
			.takenCredit(
				calculateTakenCredit(takenLectureInventory, detailGraduationResults,
					leftNormalCultureCredit, user))
			.build();
	}

	private static int calculateTakenCredit(TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults, int leftNormalCultureCredit, User user) {
		int remainCreditByDetailGraduationResult = detailGraduationResults.stream()
			.mapToInt(DetailGraduationResult::getFreeElectiveLeftCredit)
			.sum();

		int remainCreditByTakenLectures = takenLectureInventory.getTakenLectures()
			.stream()
			.mapToInt(takenLecture -> takenLecture.getLecture()
				.getCredit())
			.sum();

		int transferFreeElectiveCredit = calculateTransferFreeElectiveCredit(user);

		return remainCreditByDetailGraduationResult + remainCreditByTakenLectures
			+ leftNormalCultureCredit+transferFreeElectiveCredit;
	}
	private static int calculateTransferFreeElectiveCredit(User user) {
		if (user.getStudentCategory() == StudentCategory.TRANSFER) {
			return user.getTransferCredit().getFreeElective();
		}
		return 0;
	}

	public void checkCompleted() {
		this.isCompleted = takenCredit >= totalCredit;
	}
}
