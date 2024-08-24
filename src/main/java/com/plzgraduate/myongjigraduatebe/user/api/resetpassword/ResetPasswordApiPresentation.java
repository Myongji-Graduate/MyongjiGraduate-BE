package com.plzgraduate.myongjigraduatebe.user.api.resetpassword;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.request.ResetPasswordRequest;
import com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.response.ValidateUserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ResetPassword", description = "학번으로 유저 정보 조회 후 로그인 이아디와 일치하는지 확인하는 API")
public interface ResetPasswordApiPresentation {

	@Operation(description = "학번으로 유저 정보 조회 후 로그인 이아디와 일치하는지 확인")
	@Parameter(name = "auth-id", description = "아이디")
	ValidateUserResponse validateUser(
		@Parameter(name = "studentNumber", description = "학번", in = ParameterIn.PATH)
		@PathVariable @Pattern(regexp = "^60\\d{6}$", message = "INVALIDATED_STUDENT_NUMBER_TYPE") String studentNumber,
		@RequestParam("auth-id") String authId);

	@PatchMapping("/password")
	void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest);
}
