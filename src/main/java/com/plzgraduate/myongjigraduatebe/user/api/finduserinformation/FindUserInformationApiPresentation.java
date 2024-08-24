package com.plzgraduate.myongjigraduatebe.user.api.finduserinformation;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response.UserInformationResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FindUserInformation", description = "로그인 한 회원 정보를 조회하는 API")
public interface FindUserInformationApiPresentation {

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	UserInformationResponse getUserInformation(@LoginUser Long userId);
}
