package com.plzgraduate.myongjigraduatebe.auth.api.signin;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

import com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.request.SignInRequest;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.TokenResponse;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "SignIn", description = "로그인 API")
public interface SignInApiPresentation {

	@Operation(summary = "로그인")
	TokenResponse signIn(@Valid @RequestBody SignInRequest signInRequest);

	@Hidden
	String check(@LoginUser Long userId);
}
