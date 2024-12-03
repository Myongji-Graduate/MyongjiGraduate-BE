package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.FreeElectiveGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.NormalCultureGraduationResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RestResultResponse {

	@Schema(name = "totalCredit", example = "10")
	private final int totalCredit;
	@Schema(name = "takenCredit", example = "5")
	private final int takenCredit;
	@Schema(name = "completed", example = "false")
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
			.completed(normalCultureGraduationResult.isCompleted())
			.build();
	}

	public static RestResultResponse fromFreeElectiveResult(
		FreeElectiveGraduationResult freeElectiveGraduationResult) {
		return RestResultResponse.builder()
			.totalCredit(freeElectiveGraduationResult.getTotalCredit())
			.takenCredit(freeElectiveGraduationResult.getTakenCredit())
			.completed(freeElectiveGraduationResult.isCompleted())
			.build();
	}
}
