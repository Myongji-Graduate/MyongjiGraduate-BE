package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.auth.adaptor.in.SignInRequest;
import com.plzgraduate.myongjigraduatebe.auth.application.port.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class SignInController {

	private final SignInUseCase signInUseCase;

	@PostMapping("/sign-in")
	SignInResponse signIn(@RequestBody SignInRequest signInRequest) {
		return SignInResponse.from(signInUseCase.signIn(signInRequest));
	}

	@GetMapping("/check-login")
	String check(@LoginUser Long userId) {
		return String.valueOf(userId);
	}
}
