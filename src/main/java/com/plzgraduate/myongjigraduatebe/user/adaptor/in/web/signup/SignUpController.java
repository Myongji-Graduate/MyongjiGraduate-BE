package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.signup;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.AuthIdDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.CheckAuthIdDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.CheckStudentNumberDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.check.StudentNumberDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.signup.SignUpUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "SignUp", description = "회원가입을 진행하는 API")
public class SignUpController {

	private final SignUpUseCase signUpUseCase;
	private final CheckAuthIdDuplicationUseCase checkAuthIdDuplicationUseCase;
	private final CheckStudentNumberDuplicationUseCase checkStudentNumberDuplicationUseCase;

	@Operation(description = "회원가입을 진행한다.")
	@PostMapping("/sign-up")
	public void signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
		signUpUseCase.signUp(signUpRequest.toCommand());
	}

	@Operation(description = "로그인 아이디 중복 여부를 체크한다.")
	@Parameter(name = "auth-id", description = "아이디")
	@GetMapping("/sign-up/check-duplicate-auth-id")
	public AuthIdDuplicationResponse checkAuthIdDuplication(@RequestParam("auth-id") String authId) {
		return checkAuthIdDuplicationUseCase.checkAuthIdDuplication(authId);
	}

	@Operation(description = "학번 중복 여부를 체크한다.")
	@Parameter(name = "student-number", description = "학번")
	@GetMapping("/sign-up/check-duplicate-student-number")
	public StudentNumberDuplicationResponse checkStudentNumberDuplication(
		@RequestParam("student-number") String studentNumber) {
		return checkStudentNumberDuplicationUseCase.checkStudentNumberDuplication(studentNumber);
	}
}
