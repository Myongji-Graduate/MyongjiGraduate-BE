package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.findauthid;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserAuthIdUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.UserAuthIdResponse;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FindAuthIdController {

	private final FindUserAuthIdUseCase findUserAuthIdUseCase;

	@GetMapping("/student-number")
	public UserAuthIdResponse findUserAuthId(@Param("studentNumber") String studentNumber) {
		return findUserAuthIdUseCase.findUserAuthId(studentNumber);
	}
}
