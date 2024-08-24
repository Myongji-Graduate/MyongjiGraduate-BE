package com.plzgraduate.myongjigraduatebe.graduation.api;

import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.DetailGraduationResultResponse;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Graduations")
public interface FindDetailGraduationApiPresentation {

	@Operation(summary = "졸업 카테고리 상세 결과 조회", description = "유저의 각 졸업 카테고리 상세 결과를 조회한다.")
	@Parameter(name = "graduationCategory", description = "상세 조회하고자 하는 졸업 카테고리")
	DetailGraduationResultResponse getDetailGraduation(@LoginUser Long userId,
		@RequestParam GraduationCategory graduationCategory);
}
