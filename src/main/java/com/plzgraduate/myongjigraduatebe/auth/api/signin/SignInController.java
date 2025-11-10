package com.plzgraduate.myongjigraduatebe.auth.api.signin;

import com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.request.SignInRequest;
import com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.response.TokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.usecase.signin.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class SignInController implements SignInApiPresentation {

	private final SignInUseCase signInUseCase;

	@PostMapping("/sign-in")
	public TokenResponse signIn(@Valid @RequestBody SignInRequest signInRequest) {
		return signInUseCase.signIn(signInRequest.getAuthId(), signInRequest.getPassword());
	}

	@GetMapping("/check-login")
	public String check(@LoginUser Long userId) {
		return String.valueOf(userId);
	}
}
