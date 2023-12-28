package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.finduserinformation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.UserInformationResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "FindUserInformation", description = "로그인 한 회원 정보를 조회하는 API")
public class FindUserInformationController {

	private final FindUserInformationUseCase findUserInformationUseCase;

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	@GetMapping
	public UserInformationResponse getUserInformation(@LoginUser Long userId) {
		return findUserInformationUseCase.findUserInformation(userId);
	}
}
