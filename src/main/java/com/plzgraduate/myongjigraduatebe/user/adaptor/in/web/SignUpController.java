package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.user.application.port.SignUpUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class SignUpController {
	private final SignUpUseCase signUpUseCase;

	@PostMapping("/sign-up")
	public void signUp(@RequestBody SignUpRequest signUpRequest) {
		signUpUseCase.signUp(signUpRequest);
	}
}
