package com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.FreeElectiveGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.NormalCultureGraduationResult;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RestResultResponse {

	private final int totalCredit;
	private final int takenCredit;
	private final boolean completed;

	@Builder
	private RestResultResponse(int totalCredit, int takenCredit, boolean completed) {
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.completed = completed;
	}

	public static RestResultResponse fromNormalCultureResult(
		NormalCultureGraduationResult normalCultureGraduationResult) {
		return RestResultResponse.builder()
			.totalCredit(normalCultureGraduationResult.getTotalCredit())
			.takenCredit(normalCultureGraduationResult.getTakenCredit())
			.completed(normalCultureGraduationResult.isCompleted()).build();
	}

	public static RestResultResponse fromFreeElectiveResult(
		FreeElectiveGraduationResult freeElectiveGraduationResult) {
		return RestResultResponse.builder()
			.totalCredit(freeElectiveGraduationResult.getTotalCredit())
			.takenCredit(freeElectiveGraduationResult.getTakenCredit())
			.completed(freeElectiveGraduationResult.isCompleted()).build();
	}
}
