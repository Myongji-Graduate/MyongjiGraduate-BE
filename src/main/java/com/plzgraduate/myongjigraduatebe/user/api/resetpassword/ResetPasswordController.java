package com.plzgraduate.myongjigraduatebe.user.api.resetpassword;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.resetpassword.ResetPasswordUseCase;
import com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.response.ValidateUserResponse;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.validate.ValidateUserUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class ResetPasswordController implements ResetPasswordApiPresentation {

	private final ValidateUserUseCase validateUserUseCase;
	private final ResetPasswordUseCase resetPasswordUseCase;

	@GetMapping("/{student-number}/validate")
	public ValidateUserResponse validateUser(
		@PathVariable("student-number") String studentNumber,
		@RequestParam("auth-id") String authId) {
		return validateUserUseCase.validateUser(studentNumber, authId);
	}

	@PatchMapping("/password")
	public void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		resetPasswordUseCase.resetPassword(resetPasswordRequest.toCommand());
	}
}
