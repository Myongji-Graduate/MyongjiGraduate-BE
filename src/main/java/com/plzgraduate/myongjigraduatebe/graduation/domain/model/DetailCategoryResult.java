package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailCategoryResult {

	// 현장실습과목(haveToLectrue 제외)
	private static final List<String> EXCLUDED_HAVE_TO_LECTURE_CODES = List.of(
		"KMR01801", "KMR01802", "KMR01804", "KMR01805", "KMR01851", "KMR01852", "HAH01371", "HFC01412",
		"HFM01404", "JDC01361", "JEE01356", "JEE01357", "JEH01493", "JEH01494", "JEI01430", "JEI01467", "JEJ02549",
		"JEJ02554", "JEJ02558", "JEJ02559", "JEJ02560", "JEJ02561", "KMD02902", "KMD02903", "KMR01551", "KMR01552",
		"KMR01553", "KMR01554", "KMR01555", "KMR01560", "KMR01561", "KMR01562", "KMR01563", "KMR01564", "KMR01566",
		"KMR01567", "KMR01703", "KMR01705", "KMR01710", "KMR01712", "KMR01803", "KMR01817"
	);

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
			addHaveToLectures(taken, graduationLectures);
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

	private void addHaveToLectures(Set<Lecture> taken, Set<Lecture> graduationLectures) {
		graduationLectures.removeAll(taken);
		graduationLectures.stream()
			.filter(graduationLecture ->
				graduationLecture.getIsRevoked() == 0 && !EXCLUDED_HAVE_TO_LECTURE_CODES.contains(graduationLecture.getId()))
			.forEach(haveToLectures::add);

	}

	private boolean checkCompleted() {
		isCompleted = totalCredits <= takenCredits && isSatisfiedMandatory;
		return isCompleted;
	}
}
