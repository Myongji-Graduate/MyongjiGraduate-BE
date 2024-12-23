package com.plzgraduate.myongjigraduatebe.user.api.finduserinformation;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response.UserInformationResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FindUserInformation", description = "로그인 한 회원 정보를 조회하는 API")
public interface FindUserInformationApiPresentation {

	@SecurityRequirement(name = "AccessToken")
	UserInformationResponse getUserInformation(@Parameter(hidden = true) @LoginUser Long userId);
}
