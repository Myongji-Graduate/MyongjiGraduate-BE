package com.plzgraduate.myongjigraduatebe.completedcredit.api;

import com.plzgraduate.myongjigraduatebe.completedcredit.api.dto.CompletedCreditResponse;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "CompletedCredit")
public interface FindCompletedCreditApiPresentation {

	@Operation(summary = "기이수 학점 조회", description = "유저의 기이수 학점 조회 API")
	@SecurityRequirement(name = "AccessToken")
	List<CompletedCreditResponse> getCompletedCredits(@Parameter(hidden = true) @LoginUser Long userId);
}
