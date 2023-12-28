package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.resetpassword;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.resetpassword.ResetPasswordUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.validate.ValidateUserResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.validate.ValidateUserUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name = "ResetPassword", description = "학번으로 유저 정보 조회 후 로그인 이아디와 일치하는지 확인하는 API")
public class ResetPasswordController {

	private final ValidateUserUseCase validateUserUseCase;
	private final ResetPasswordUseCase resetPasswordUseCase;

	@Operation(description = "학번으로 유저 정보 조회 후 로그인 이아디와 일치하는지 확인")
	@Parameter(name = "auth-id", description = "아이디")
	@GetMapping("/{student-number}/validate")
	public ValidateUserResponse validateUser(
		@Parameter(name = "studentNumber", description = "학번", in = ParameterIn.PATH)
		@PathVariable("student-number") String studentNumber,
		@RequestParam("auth-id") String authId) {
		return validateUserUseCase.validateUser(studentNumber, authId);
	}

	@Operation(description = "아이디로 유저 정보 조회 후 비밀번호를 변경")
	@PatchMapping("/password")
	public void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		resetPasswordUseCase.resetPassword(resetPasswordRequest.toCommand());
	}
}
