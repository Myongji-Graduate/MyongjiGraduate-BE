package com.plzgraduate.myongjigraduatebe.user.api.signup;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.request.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.AuthIdDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.StudentNumberDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.check.CheckAuthIdDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.check.CheckStudentNumberDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.signup.SignUpUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class SignUpController implements SignUpApiPresentation {

	private final SignUpUseCase signUpUseCase;
	private final CheckAuthIdDuplicationUseCase checkAuthIdDuplicationUseCase;
	private final CheckStudentNumberDuplicationUseCase checkStudentNumberDuplicationUseCase;

	@PostMapping("/sign-up")
	public void signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
		signUpUseCase.signUp(signUpRequest.toCommand());
	}

	@GetMapping("/sign-up/check-duplicate-auth-id")
	public AuthIdDuplicationResponse checkAuthIdDuplication(
		@RequestParam("auth-id") @Size(min = 6, max = 20, message = "INVALIDATED_AUTHID_TYPE") String authId) {
		return checkAuthIdDuplicationUseCase.checkAuthIdDuplication(authId);
	}

	@GetMapping("/sign-up/check-duplicate-student-number")
	public StudentNumberDuplicationResponse checkStudentNumberDuplication(
		@RequestParam("student-number") @Pattern(regexp = "^60\\d{6}$", message = "INVALIDATED_STUDENT_NUMBER_TYPE") String studentNumber) {
		return checkStudentNumberDuplicationUseCase.checkStudentNumberDuplication(studentNumber);
	}
}
