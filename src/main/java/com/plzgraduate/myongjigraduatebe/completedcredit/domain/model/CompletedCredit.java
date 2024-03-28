package com.plzgraduate.myongjigraduatebe.completedcredit.domain.model;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CompletedCredit {

	private final GraduationCategory category;
	private final int totalCredit;
	private final int takenCredit;

	@Builder
	private CompletedCredit(GraduationCategory category, int totalCredit, int takenCredit) {
		this.category = category;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}
}
