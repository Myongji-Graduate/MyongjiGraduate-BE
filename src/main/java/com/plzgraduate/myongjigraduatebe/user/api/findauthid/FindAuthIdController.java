package com.plzgraduate.myongjigraduatebe.user.api.findauthid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserAuthIdUseCase;
import com.plzgraduate.myongjigraduatebe.user.api.findauthid.dto.response.UserAuthIdResponse;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FindAuthIdController implements FindAuthIdApiPresentation {

	private final FindUserAuthIdUseCase findUserAuthIdUseCase;

	@GetMapping("/{student-number}/auth-id")
	public UserAuthIdResponse findUserAuthId(@PathVariable("student-number") String studentNumber) {
		return findUserAuthIdUseCase.findUserAuthId(studentNumber);
	}
}
