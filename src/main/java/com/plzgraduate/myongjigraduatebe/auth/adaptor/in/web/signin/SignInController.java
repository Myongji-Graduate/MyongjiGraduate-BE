package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.signin;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.signin.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.TokenResponse;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "SignIn", description = "로그인 API")
public class SignInController {

	private final SignInUseCase signInUseCase;

	@Operation(summary = "로그인")
	@PostMapping("/sign-in")
	public TokenResponse signIn(@Valid @RequestBody SignInRequest signInRequest) {
		return signInUseCase.signIn(signInRequest.toCommand());
	}

	@Hidden
	@GetMapping("/check-login")
	public String check(@LoginUser Long userId) {
		return String.valueOf(userId);
	}
}
