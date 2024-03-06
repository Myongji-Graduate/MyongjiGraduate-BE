package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationResultResponse {

	@Schema(name = "totalCredit", example = "12")
	private final int totalCredit;
	@Schema(name = "takenCredit", example = "9")
	private final double takenCredit;
	private final List<DetailGraduationCategoryResultResponse> detailCategory;
	@Schema(name = "completed", example = "false")
	private final boolean completed;

	@Builder
	private DetailGraduationResultResponse(int totalCredit, double takenCredit,
		List<DetailGraduationCategoryResultResponse> detailCategory, boolean completed) {
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.detailCategory = detailCategory;
		this.completed = completed;
	}

	public static DetailGraduationResultResponse from(DetailGraduationResult detailGraduationResult) {
		return DetailGraduationResultResponse.builder()
			.totalCredit(detailGraduationResult.getTotalCredit())
			.takenCredit(detailGraduationResult.getTakenCredit())
			.detailCategory(detailGraduationResult.getDetailCategory().stream()
				.map(DetailGraduationCategoryResultResponse::from)
				.collect(Collectors.toList()))
			.completed(detailGraduationResult.isCompleted()).build();
	}
}
