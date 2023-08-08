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
	private final List<Lecture> takenMandatoryLectures = new ArrayList<>();
	private final List<Lecture> haveToMandatoryLectures = new ArrayList<>();
	private final List<Lecture> takenElectiveLectures = new ArrayList<>();
	private final List<Lecture> haveToElectiveLectures = new ArrayList<>();

	@Builder
	private DetailCategoryResult(String detailCategoryName, boolean isCompleted, boolean isSatisfiedMandatory,
		int totalCredits, int takenCredits) {
		this.detailCategoryName = detailCategoryName;
		this.isCompleted = isCompleted;
		this.isSatisfiedMandatory = isSatisfiedMandatory;
		this.totalCredits = totalCredits;
		this.takenCredits = takenCredits;
	}

	public static DetailCategoryResult create(String detailCategoryName, boolean isSatisfiedMandatory,
		int totalCredits) {
		return DetailCategoryResult.builder()
			.detailCategoryName(detailCategoryName)
			.isCompleted(false)
			.isSatisfiedMandatory(isSatisfiedMandatory)
			.totalCredits(totalCredits)
			.takenCredits(0)
			.build();
	}

	public void calculate(Set<Lecture> taken, Set<Lecture> basicAcademicalLectures) {
		addTakenLectures(taken);
		if(!checkCompleted()) {
			addMandatoryLectures(taken, basicAcademicalLectures);
		}
	}

	private void addTakenLectures(Set<Lecture> taken) {
		taken.forEach(lecture -> {
			takenMandatoryLectures.add(lecture);
			takenCredits += lecture.getCredit();
		});
	}

	private void addMandatoryLectures(Set<Lecture> taken, Set<Lecture> basicAcademicalLectures) {
		basicAcademicalLectures.removeAll(taken);
		haveToMandatoryLectures.addAll(basicAcademicalLectures);
	}

	private boolean checkCompleted() {
		isCompleted = totalCredits <= takenCredits && isSatisfiedMandatory;
		return isCompleted;
	}
}
