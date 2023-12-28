package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.withdraw;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.withdraw.WithDrawUserUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "WithDraw", description = "유저의 회원 탈퇴 요청을 수행하는 API")
public class WithDrawController {

	private final WithDrawUserUseCase withDrawUserUseCase;

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	@DeleteMapping()
	public void withDraw(@LoginUser Long userId, @RequestBody WithDrawRequest withDrawRequest) {
		withDrawUserUseCase.withDraw(userId, withDrawRequest.toCommand());
	}
}
