package com.plzgraduate.myongjigraduatebe.graduation.application.dto;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResolvedDetailGraduation {

	private final CalculateDetailGraduationUseCase calculateDetailGraduationUseCase;
	private final int graduationCategoryTotalCredit;

	@Builder
	private ResolvedDetailGraduation(CalculateDetailGraduationUseCase calculateDetailGraduationUseCase,
		int graduationCategoryTotalCredit) {
		this.calculateDetailGraduationUseCase = calculateDetailGraduationUseCase;
		this.graduationCategoryTotalCredit = graduationCategoryTotalCredit;
	}
}
