package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.resetpassword;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.resetpassword.ResetPasswordUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("api/v1/users/password")
@RequiredArgsConstructor
public class ResetPasswordController {

	private final ResetPasswordUseCase resetPasswordUseCase;

	@PatchMapping
	public void resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		resetPasswordUseCase.resetPassword(resetPasswordRequest.toCommand());
	}
}
