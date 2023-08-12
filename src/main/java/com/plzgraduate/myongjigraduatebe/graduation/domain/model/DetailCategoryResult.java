package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailCategoryResult {

	private final String detailCategoryName;
	private boolean isCompleted;
	private final boolean isSatisfiedMandatory;
	private final int totalCredits;
	private int takenCredits;
	private int normalLeftCredit;
	private int freeElectiveLeftCredit;
	private final List<Lecture> takenMandatoryLectures = new ArrayList<>();
	private final List<Lecture> haveToMandatoryLectures = new ArrayList<>();
	private final List<Lecture> takenElectiveLectures = new ArrayList<>();
	private final List<Lecture> haveToElectiveLectures = new ArrayList<>();

	@Builder
	private DetailCategoryResult(String detailCategoryName, boolean isCompleted, boolean isSatisfiedMandatory,
		int totalCredits, int takenCredits, int normalLeftCredit, int freeElectiveLeftCredit) {
		this.detailCategoryName = detailCategoryName;
		this.isCompleted = isCompleted;
		this.isSatisfiedMandatory = isSatisfiedMandatory;
		this.totalCredits = totalCredits;
		this.takenCredits = takenCredits;
		this.normalLeftCredit = normalLeftCredit;
		this.freeElectiveLeftCredit = freeElectiveLeftCredit;
	}

	public static DetailCategoryResult create(String detailCategoryName, boolean isSatisfiedMandatory,
		int totalCredits) {
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

	public void calculate(Set<Lecture> taken, Set<Lecture> graduationLectures) {
		addTakenLectures(taken);
		calculateLeftCredit();
		if(!checkCompleted()) {
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
			takenMandatoryLectures.add(lecture);
			takenCredits += lecture.getCredit();
		});
	}

	private void calculateLeftCredit() {
		int leftCredit = takenCredits - totalCredits;
		if (leftCredit > 0) {
			if (detailCategoryName.equals("전공")) {
				freeElectiveLeftCredit = leftCredit;
			}
			normalLeftCredit = leftCredit;
		}
	}

	private void addMandatoryLectures(Set<Lecture> taken, Set<Lecture> graduationLectures) {
		graduationLectures.removeAll(taken);
		haveToMandatoryLectures.addAll(graduationLectures);
	}

	private boolean checkCompleted() {
		isCompleted = totalCredits <= takenCredits && isSatisfiedMandatory;
		return isCompleted;
	}
}
