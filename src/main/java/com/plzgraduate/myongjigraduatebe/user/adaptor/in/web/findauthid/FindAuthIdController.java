package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.findauthid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserAuthIdUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.UserAuthIdResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "FindAuthId", description = "학번으로 해당 학생의 아이디를 조회하는 API")
public class FindAuthIdController {

	private final FindUserAuthIdUseCase findUserAuthIdUseCase;

	@GetMapping("/{student-number}/auth-id")
	public UserAuthIdResponse findUserAuthId(
		@Parameter(name = "studentNumber", description = "학번", in = ParameterIn.PATH) @PathVariable("student-number") String studentNumber) {
		return findUserAuthIdUseCase.findUserAuthId(studentNumber);
	}
}
