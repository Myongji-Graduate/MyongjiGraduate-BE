package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.finduserinformation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.UserInformationResponse;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FindUserInformationController {

	private final FindUserInformationUseCase findUserInformationUseCase;

	@GetMapping
	public UserInformationResponse getUserInformation(@LoginUser Long userId) {
		return findUserInformationUseCase.findUserInformation(userId);
	}
}
