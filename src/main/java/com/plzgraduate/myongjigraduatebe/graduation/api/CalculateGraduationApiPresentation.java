package com.plzgraduate.myongjigraduatebe.graduation.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.GraduationResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CalculateGraduation", description = "유저의 졸업 결과를 계산하는 API")
public interface CalculateGraduationApiPresentation {

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	GraduationResponse calculate(@LoginUser Long userId);
}
