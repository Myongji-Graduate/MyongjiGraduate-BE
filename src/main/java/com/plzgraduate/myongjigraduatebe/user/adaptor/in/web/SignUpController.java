package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.request.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.SignUpUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
class SignUpController {

	private final SignUpUseCase signUpUseCase;

	@PostMapping("/sign-up")
	public void signUp(@RequestBody SignUpRequest signUpRequest) {
		signUpUseCase.signUp(signUpRequest.toCommand());
	}
}
