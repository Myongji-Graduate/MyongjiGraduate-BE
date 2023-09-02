package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.request.SignInRequest;
import com.plzgraduate.myongjigraduatebe.auth.application.port.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.auth.application.port.response.SignInResponse;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
class SignInController {

	private final SignInUseCase signInUseCase;

	@PostMapping("/sign-in")
	SignInResponse signIn(@RequestBody SignInRequest signInRequest) {
		return signInUseCase.signIn(signInRequest.toCommand());
	}

	@GetMapping("/check-login")
	String check(@LoginUser Long userId) {
		return String.valueOf(userId);
	}
}
