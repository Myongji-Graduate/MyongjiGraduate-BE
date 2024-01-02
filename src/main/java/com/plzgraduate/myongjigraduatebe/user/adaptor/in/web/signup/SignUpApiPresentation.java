package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.signup;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.AuthIdDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.StudentNumberDuplicationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "SignUp", description = "회원가입을 진행하는 API")
public interface SignUpApiPresentation {

	@Operation(description = "회원가입을 진행한다.")
	void signUp(@Valid @RequestBody SignUpRequest signUpRequest);

	@Operation(description = "로그인 아이디 중복 여부를 체크한다.")
	@Parameter(name = "auth-id", description = "아이디")
	AuthIdDuplicationResponse checkAuthIdDuplication(@RequestParam("auth-id") String authId);

	@Operation(description = "학번 중복 여부를 체크한다.")
	@Parameter(name = "student-number", description = "학번")
	StudentNumberDuplicationResponse checkStudentNumberDuplication(
		@RequestParam("student-number") String studentNumber);
}
