package com.plzgraduate.myongjigraduatebe.graduation.api;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.DetailGraduationResultResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateSingleDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/graduations/detail")
@RequiredArgsConstructor
public class FindDetailGraduationController implements FindDetailGraduationApiPresentation {

	private final CalculateSingleDetailGraduationUseCase calculateSingleDetailGraduationUseCase;

	@GetMapping
	public DetailGraduationResultResponse getDetailGraduation(@LoginUser Long userId,
		@RequestParam GraduationCategory graduationCategory) {
		DetailGraduationResult detailGraduationResult = calculateSingleDetailGraduationUseCase.calculateSingleDetailGraduation(
			userId, graduationCategory);
		return DetailGraduationResultResponse.from(detailGraduationResult);
	}
}
