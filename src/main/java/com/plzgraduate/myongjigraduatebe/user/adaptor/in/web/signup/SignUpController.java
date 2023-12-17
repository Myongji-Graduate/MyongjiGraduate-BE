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

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class SignUpController {

	private final SignUpUseCase signUpUseCase;
	private final CheckAuthIdDuplicationUseCase checkAuthIdDuplicationUseCase;
	private final CheckStudentNumberDuplicationUseCase checkStudentNumberDuplicationUseCase;

	@PostMapping("/sign-up")
	public void signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
		signUpUseCase.signUp(signUpRequest.toCommand());
	}

	@GetMapping("/sign-up/check-duplicate-auth-id")
	public AuthIdDuplicationResponse checkAuthIdDuplication(@RequestParam("auth-id") String authId) {
		return checkAuthIdDuplicationUseCase.checkAuthIdDuplication(authId);
	}

	@GetMapping("/sign-up/check-duplicate-student-number")
	public StudentNumberDuplicationResponse checkStudentNumberDuplication(
		@RequestParam("student-number") String studentNumber) {
		return checkStudentNumberDuplicationUseCase.checkStudentNumberDuplication(studentNumber);
	}
}
