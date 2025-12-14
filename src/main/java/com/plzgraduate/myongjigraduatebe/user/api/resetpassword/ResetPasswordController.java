package com.plzgraduate.myongjigraduatebe.user.api.resetpassword;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.request.ResetPasswordRequest;
import com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.response.ValidateUserResponse;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.resetpassword.ResetPasswordUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.validate.ValidateUserUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@WebAdapter
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Validated
public class ResetPasswordController implements ResetPasswordApiPresentation {

	private final ValidateUserUseCase validateUserUseCase;
	private final ResetPasswordUseCase resetPasswordUseCase;

	@GetMapping("/{studentNumber}/validate")
	public ValidateUserResponse validateUser(
		@PathVariable @Pattern(regexp = "^60\\d{6}$", message = "INVALIDATED_STUDENT_NUMBER_TYPE") String studentNumber,
		@RequestParam("auth-id") String authId) {
		boolean validated = validateUserUseCase.validateUser(studentNumber, authId);
		return ValidateUserResponse.builder()
			.passedUserValidation(validated)
			.build();
	}

	@PatchMapping("/password")
	public void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		resetPasswordUseCase.resetPassword(resetPasswordRequest.getAuthId(),
			resetPasswordRequest.getNewPassword(),
			resetPasswordRequest.getPasswordCheck());
	}
}
