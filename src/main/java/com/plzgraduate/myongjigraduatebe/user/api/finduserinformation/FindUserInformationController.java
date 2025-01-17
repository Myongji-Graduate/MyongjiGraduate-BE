package com.plzgraduate.myongjigraduatebe.user.api.finduserinformation;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response.UserInformationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class FindUserInformationController implements FindUserInformationApiPresentation {

	private final FindUserInformationUseCase findUserInformationUseCase;

	@GetMapping
	public UserInformationResponse getUserInformation(@LoginUser Long userId) {
		User user = findUserInformationUseCase.findUserInformation(userId);
		return UserInformationResponse.from(user);
	}
}
