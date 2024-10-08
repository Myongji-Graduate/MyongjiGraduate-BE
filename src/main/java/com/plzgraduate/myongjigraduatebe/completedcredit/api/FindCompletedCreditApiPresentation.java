package com.plzgraduate.myongjigraduatebe.completedcredit.api;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.completedcredit.api.dto.CompletedCreditResponse;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CompletdCredit")
public interface FindCompletedCreditApiPresentation {

	@Operation(summary = "기이수 학점 조회", description = "유저의 기이수 학점 조회 API")
	@Parameter(name = "userId", description = "로그인한 유저의 PK값 - 자동 적용")
	List<CompletedCreditResponse> getCompletedCredits(@LoginUser Long userId);
}
