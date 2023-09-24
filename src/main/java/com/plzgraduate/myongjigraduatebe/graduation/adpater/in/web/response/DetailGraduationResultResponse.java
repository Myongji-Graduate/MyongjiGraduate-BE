package com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationResultResponse {

	private final int totalCredit;
	private final int takenCredit;
	private final List<DetailGraduationCategoryResultResponse> detailCategory;
	private final boolean completed;

	@Builder
	private DetailGraduationResultResponse(int totalCredit, int takenCredit,
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
