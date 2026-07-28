package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.NORMAL_CULTURE;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NormalCultureGraduationResult {

	private static final String VOLUNTEER_CREDIT_CODE = "KMA02198";
	private static final String M_FRESHMAN_SEMINAR = HonorsCollegeMajorPolicy.M_FRESHMAN_SEMINAR;

	private final String categoryName;
	private final int totalCredit;
	private boolean isCompleted;
	private int takenCredit;
	private final boolean mFreshmanSeminarRequired;
	private final boolean mFreshmanSeminarCompleted;

	@Builder
	private NormalCultureGraduationResult(
		String categoryName,
		boolean isCompleted,
		int totalCredit,
		int takenCredit,
		boolean mFreshmanSeminarRequired,
		boolean mFreshmanSeminarCompleted
	) {
		this.categoryName = categoryName;
		this.isCompleted = isCompleted;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.mFreshmanSeminarRequired = mFreshmanSeminarRequired;
		this.mFreshmanSeminarCompleted = mFreshmanSeminarCompleted;
	}

	public static NormalCultureGraduationResult create(
		int totalCredit,
		int acknowledgedCredit,
		TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults
	) {
		return create(totalCredit, acknowledgedCredit, takenLectureInventory, detailGraduationResults,
			User.builder().build());
	}

	public static NormalCultureGraduationResult create(
		int totalCredit,
		int acknowledgedCredit,
		TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults,
		User user
	) {
		boolean mFreshmanSeminarRequired = HonorsCollegeMajorPolicy.requiresMFreshmanSeminar(user);
		boolean mFreshmanSeminarCompleted = takenLectureInventory.getTakenLectures().stream()
			.anyMatch(takenLecture -> M_FRESHMAN_SEMINAR.equals(takenLecture.getLecture().getId()));
		return NormalCultureGraduationResult.builder()
			.categoryName(NORMAL_CULTURE.getName())
			.isCompleted(false)
			.totalCredit(totalCredit)
			.takenCredit(calculateTakenCredit(acknowledgedCredit, takenLectureInventory, detailGraduationResults))
			.mFreshmanSeminarRequired(mFreshmanSeminarRequired)
			.mFreshmanSeminarCompleted(mFreshmanSeminarCompleted)
			.build();
	}

	private static int calculateTakenCredit(
		int acknowledgedCredit,
		TakenLectureInventory takenLectureInventory,
		List<DetailGraduationResult> detailGraduationResults
	) {
		int remainCreditByDetailGraduationResult = acknowledgedCredit + detailGraduationResults.stream()
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
		this.isCompleted = takenCredit >= totalCredit
			&& (!mFreshmanSeminarRequired || mFreshmanSeminarCompleted);
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
