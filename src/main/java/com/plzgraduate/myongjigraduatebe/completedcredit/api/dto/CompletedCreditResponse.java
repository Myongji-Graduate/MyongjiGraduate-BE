package com.plzgraduate.myongjigraduatebe.completedcredit.api.dto;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompletedCreditResponse {

	private String category;
	private int totalCredit;
	private double takenCredit;
	private boolean completed;

	@Builder
	private CompletedCreditResponse(String category, int totalCredit, double takenCredit, boolean completed) {
		this.category = category;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.completed = completed;
	}

	public static CompletedCreditResponse from(CompletedCredit completedCredit) {
		return CompletedCreditResponse.builder()
			.category(completedCredit.getGraduationCategory().name())
			.totalCredit(completedCredit.getTotalCredit())
			.takenCredit(completedCredit.getTakenCredit())
			.completed(completedCredit.getTakenCredit() >= completedCredit.getTotalCredit())
			.build();
	}
}
