package com.plzgraduate.myongjigraduatebe.user.api.findauthid;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.api.findauthid.dto.response.UserAuthIdResponse;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserAuthIdUseCase;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class FindAuthIdController implements FindAuthIdApiPresentation {

	private final FindUserAuthIdUseCase findUserAuthIdUseCase;

	@GetMapping("/{studentNumber}/auth-id")
	public UserAuthIdResponse findUserAuthId(
		@PathVariable @Pattern(regexp = "^60\\d{6}$", message = "INVALIDATED_STUDENT_NUMBER_TYPE") String studentNumber) {
		String foundUserAuthId = findUserAuthIdUseCase.findUserAuthId(studentNumber);
		return UserAuthIdResponse.of(foundUserAuthId, studentNumber);
	}
}
