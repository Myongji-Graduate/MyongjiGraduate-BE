package com.plzgraduate.myongjigraduatebe.graduation.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.DetailGraduationResultResponse;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "FindDetailGraduation")
public interface FindDetailGraduationApiPresentation {

	@Operation(summary = "졸업 카테고리 상세 결과 조회", description = "유저의 각 졸업 카테고리 상세 결과를 조회한다.")
	@Parameter(name = "graduationCategory", description = "상세 조회하고자 하는 졸업 카테고리")
	@SecurityRequirement(name = "AccessToken")
	DetailGraduationResultResponse getDetailGraduation(
		@Parameter(hidden = true) @LoginUser Long userId,
		@RequestParam GraduationCategory graduationCategory
	);
}
