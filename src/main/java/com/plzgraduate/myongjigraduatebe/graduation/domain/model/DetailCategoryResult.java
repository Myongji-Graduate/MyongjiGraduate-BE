package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailCategoryResult {

	private final boolean isSatisfiedMandatory;
	private final int totalCredits;
	private final List<Lecture> takenLectures = new ArrayList<>();
	private final List<Lecture> haveToLectures = new ArrayList<>();
	private String detailCategoryName;
	private boolean isCompleted;
	private int takenCredits;
	private int normalLeftCredit;
	private int freeElectiveLeftCredit;
	@Builder
	private DetailCategoryResult(
		String detailCategoryName, boolean isCompleted,
		boolean isSatisfiedMandatory,
		int totalCredits, int takenCredits, int normalLeftCredit, int freeElectiveLeftCredit
	) {
		this.detailCategoryName = detailCategoryName;
		this.isCompleted = isCompleted;
		this.isSatisfiedMandatory = isSatisfiedMandatory;
		this.totalCredits = totalCredits;
		this.takenCredits = takenCredits;
		this.normalLeftCredit = normalLeftCredit;
		this.freeElectiveLeftCredit = freeElectiveLeftCredit;
	}

	public static DetailCategoryResult create(
		String detailCategoryName,
		boolean isSatisfiedMandatory,
		int totalCredits
	) {
		return DetailCategoryResult.builder()
			.detailCategoryName(detailCategoryName)
			.isCompleted(false)
			.isSatisfiedMandatory(isSatisfiedMandatory)
			.totalCredits(totalCredits)
			.takenCredits(0)
			.normalLeftCredit(0)
			.freeElectiveLeftCredit(0)
			.build();
	}

	@Override
	public String toString() {
		return "DetailCategoryResult{" +
			"isSatisfiedMandatory=" + isSatisfiedMandatory +
			", totalCredits=" + totalCredits +
			", takenLectures=" + takenLectures +
			", haveToLectures=" + haveToLectures +
			", detailCategoryName='" + detailCategoryName + '\'' +
			", isCompleted=" + isCompleted +
			", takenCredits=" + takenCredits +
			", normalLeftCredit=" + normalLeftCredit +
			", freeElectiveLeftCredit=" + freeElectiveLeftCredit +
			'}';
	}

	public void assignDetailCategoryName(String detailCategoryName) {
		this.detailCategoryName = detailCategoryName;
	}

	public void calculate(Set<Lecture> taken, Set<Lecture> graduationLectures) {

			addTakenLectures(taken);
			calculateLeftCredit();
			if (!checkCompleted()) {
				addMandatoryLectures(taken, graduationLectures);
			}

	}

	public void addNormalLeftCredit(int normalLeftCredit) {
		this.normalLeftCredit += normalLeftCredit;
	}

	public void addFreeElectiveLeftCredit(int freeElectiveLeftCredit) {
		this.freeElectiveLeftCredit += freeElectiveLeftCredit;
	}

	private void addTakenLectures(Set<Lecture> taken) {
		taken.forEach(lecture -> {
			takenLectures.add(lecture);
			takenCredits += lecture.getCredit();
		});
	}

	private void calculateLeftCredit() {
		int leftCredit = takenCredits - totalCredits;
		if (leftCredit > 0) {
			if (detailCategoryName.equals(PRIMARY_MANDATORY_MAJOR.getName()) ||
				detailCategoryName.equals(PRIMARY_ELECTIVE_MAJOR.getName())) {
				freeElectiveLeftCredit = leftCredit;
				takenCredits -= leftCredit;
				return;
			}
			normalLeftCredit = leftCredit;
			takenCredits -= leftCredit;
		}
	}

	private void addMandatoryLectures(Set<Lecture> taken, Set<Lecture> graduationLectures) {
		graduationLectures.removeAll(taken);
		graduationLectures.stream()
			.filter(graduationLecture -> graduationLecture.getIsRevoked() == 0)
			.forEach(haveToLectures::add);

	}

	private boolean checkCompleted() {
		isCompleted = totalCredits <= takenCredits && isSatisfiedMandatory;
		return isCompleted;
	}
}
