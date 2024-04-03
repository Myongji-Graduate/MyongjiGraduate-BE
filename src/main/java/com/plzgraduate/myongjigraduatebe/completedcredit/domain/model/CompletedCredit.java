package com.plzgraduate.myongjigraduatebe.completedcredit.domain.model;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CompletedCredit {

	private final Long id;
	private final GraduationCategory graduationCategory;
	private final int totalCredit;
	private final double takenCredit;

	@Builder
	private CompletedCredit(Long id,  GraduationCategory graduationCategory, int totalCredit,
		double takenCredit) {
		this.id = id;
		this.graduationCategory = graduationCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}
}
