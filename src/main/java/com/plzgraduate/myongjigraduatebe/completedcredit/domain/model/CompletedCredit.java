package com.plzgraduate.myongjigraduatebe.completedcredit.domain.model;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CompletedCredit {

	private final Long id;
	private final Long userId;
	private final GraduationCategory graduationCategory;
	private final int totalCredit;
	private final double takenCredit;

	@Builder
	private CompletedCredit(Long id, Long userId, GraduationCategory graduationCategory, int totalCredit, double takenCredit) {
		this.id = id;
		this.userId = userId;
		this.graduationCategory = graduationCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}
}
